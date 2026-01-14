package com.assignment.backend.repository;

import com.assignment.backend.model.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndUserUsername(Long id, String username);

    List<Task> findByUserUsername(String username);
    List<Task> findByUserUsername(String username, Sort sort);

    List<Task> findByUserUsernameAndTitleContainingIgnoreCase(String username, String q, Sort sort);

    List<Task> findByUserUsernameAndStatus(String username, Task.Status status, Sort sort);

    List<Task> findByUserUsernameAndPriority(String username, Task.Priority priority, Sort sort);
}
