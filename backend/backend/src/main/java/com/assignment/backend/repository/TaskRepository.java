package com.assignment.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.backend.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
}
