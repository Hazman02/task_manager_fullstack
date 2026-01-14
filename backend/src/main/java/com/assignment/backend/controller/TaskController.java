package com.assignment.backend.controller;

import com.assignment.backend.model.Task;
import com.assignment.backend.model.User;
import com.assignment.backend.repository.TaskRepository;
import com.assignment.backend.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // ✅ Search / filter / sorting (per-user)
    // Examples:
    // /api/tasks?q=work
    // /api/tasks?status=COMPLETED
    // /api/tasks?priority=HIGH
    // /api/tasks?sort=dueDate,asc
    @GetMapping
    public List<Task> getAll(
            Authentication auth,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) Task.Priority priority,
            @RequestParam(required = false) String sort
    ) {
        String username = auth.getName();

        Sort sortObj = Sort.by("id").descending();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            String dir = (parts.length > 1) ? parts[1].trim().toLowerCase() : "asc";
            sortObj = dir.equals("desc") ? Sort.by(field).descending() : Sort.by(field).ascending();
        }

        // priority filter
        if (priority != null) {
            return taskRepository.findByUserUsernameAndPriority(username, priority, sortObj);
        }

        // status filter
        if (status != null) {
            return taskRepository.findByUserUsernameAndStatus(username, status, sortObj);
        }

        // search
        if (q != null && !q.isBlank()) {
            return taskRepository.findByUserUsernameAndTitleContainingIgnoreCase(username, q.trim(), sortObj);
        }

        return taskRepository.findByUserUsername(username, sortObj);
    }

    // ✅ Create task for logged-in user
    @PostMapping
    public Task create(@RequestBody Task task, Authentication auth) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setId(null);
        task.setUser(user);

        // keep status consistent if client sends "completed"
        if (task.getStatus() == null) task.setStatus(Task.Status.PENDING);
        if (task.getPriority() == null) task.setPriority(Task.Priority.MEDIUM);

        return taskRepository.save(task);
    }

    // ✅ Update (only if belongs to user)
    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @RequestBody Task updated, Authentication auth) {
        String username = auth.getName();

        Task task = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (updated.getTitle() != null && !updated.getTitle().trim().isEmpty()) {
            task.setTitle(updated.getTitle());
        }
        task.setDescription(updated.getDescription());
        if (updated.getPriority() != null) task.setPriority(updated.getPriority());
        if (updated.getStatus() != null) task.setStatus(updated.getStatus());
        task.setDueDate(updated.getDueDate());

        return taskRepository.save(task);
    }

    // ✅ Delete (only if belongs to user)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();

        Task task = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        taskRepository.delete(task);
    }
}
