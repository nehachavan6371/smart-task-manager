package com.smarttaskmanager.controller;

import com.smarttaskmanager.repository.TaskRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    private final TaskRepository taskRepository;

    public HealthController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        long tasks = 0;
        try { tasks = taskRepository.count(); } catch (Exception ignored) {}
        return Map.of("status", "UP", "tasks", tasks);
    }

    @GetMapping("/ready")
    public Map<String, Object> ready() {
        // simple readiness check
        try { taskRepository.count(); return Map.of("ready", true); } catch (Exception e) { return Map.of("ready", false); }
    }
}
