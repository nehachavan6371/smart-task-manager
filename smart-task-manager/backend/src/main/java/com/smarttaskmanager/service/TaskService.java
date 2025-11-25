package com.smarttaskmanager.service;

import com.smarttaskmanager.model.AuditLog;
import com.smarttaskmanager.model.Task;
import com.smarttaskmanager.repository.AuditLogRepository;
import com.smarttaskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final AuditLogRepository auditLogRepository;

    public TaskService(TaskRepository taskRepository, AuditLogRepository auditLogRepository) {
        this.taskRepository = taskRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<Task> listAll() { return taskRepository.findAll(); }

    public Optional<Task> findById(Long id) { return taskRepository.findById(id); }

    @Transactional
    public Task create(Task task, String performedBy) {
        Task saved = taskRepository.save(task);
        writeAudit(saved, "CREATE", performedBy);
        return saved;
    }

    @Transactional
    public Task update(Long id, Task partial, String performedBy) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (partial.getTitle() != null) task.setTitle(partial.getTitle());
        if (partial.getDescription() != null) task.setDescription(partial.getDescription());
        if (partial.getAssignee() != null) task.setAssignee(partial.getAssignee());
        if (partial.getStatus() != null) task.setStatus(partial.getStatus());
        Task saved = taskRepository.save(task);
        writeAudit(saved, "UPDATE", performedBy);
        return saved;
    }

    @Transactional
    public void delete(Long id, String performedBy) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        taskRepository.delete(task);
        writeAudit(task, "DELETE", performedBy);
    }

    private void writeAudit(Task task, String action, String performedBy) {
        AuditLog a = new AuditLog();
        a.setEntityType("Task");
        a.setEntityId(task.getId());
        a.setAction(action);
        a.setPerformedBy(performedBy == null ? "system" : performedBy);
        String details = String.format("title=%s;assignee=%s;status=%s", task.getTitle(), task.getAssignee(), task.getStatus());
        a.setDetails(details);
        auditLogRepository.save(a);
    }
}
