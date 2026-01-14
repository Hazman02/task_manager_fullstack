package com.assignment.backend.service;

import com.assignment.backend.model.Task;
import com.assignment.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getUserTasks(String username) {
        return taskRepository.findByUserUsername(username);
    }

    public Task createTask(Task task, String username) {
        task.setCreatedAt(LocalDateTime.now());

        if (task.getStatus() == null) {
            task.setStatus(Task.Status.PENDING);
        }

        if (task.getPriority() == null) {
            task.setPriority(Task.Priority.MEDIUM);
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask, String username) {
        Task task = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (updatedTask.getTitle() != null) {
            task.setTitle(updatedTask.getTitle());
        }

        if (updatedTask.getDescription() != null) {
            task.setDescription(updatedTask.getDescription());
        }

        if (updatedTask.getStatus() != null) {
            task.setStatus(updatedTask.getStatus());
        }

        if (updatedTask.getPriority() != null) {
            task.setPriority(updatedTask.getPriority());
        }

        if (updatedTask.getDueDate() != null) {
            task.setDueDate(updatedTask.getDueDate());
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id, String username) {
        Task task = taskRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        taskRepository.delete(task);
    }
}
