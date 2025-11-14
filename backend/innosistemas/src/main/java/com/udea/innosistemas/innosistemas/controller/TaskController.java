package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.udea.innosistemas.innosistemas.entity.Task;
import com.udea.innosistemas.innosistemas.service.TaskService;

@RestController
@RequestMapping("/project/task")
public class TaskController {


    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/save")
    public ResponseEntity<Task> saveTask(@RequestBody Task task) {
        return handleTaskSaveOrUpdate(() -> taskService.saveTask(task));
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<Task>> listAllTasks() {
        return ResponseEntity.ok(taskService.listAllTasks());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    public ResponseEntity<Task> updatedTask(@RequestBody Task task) {
        return handleTaskSaveOrUpdate(() -> taskService.saveTask(task));
    }
    
    @PutMapping("updateState/{idTask}/{idState}")
    public ResponseEntity<Void> updateState(@PathVariable Long idTask, @PathVariable Long idState) {
        log.info("[TaskController] updateState called with taskId={} stateId={}", idTask, idState);
        return handleUpdateState(idTask, idState);
    }

    private ResponseEntity<Task> handleTaskSaveOrUpdate(TaskSupplier supplier) {
        try {
            Task result = supplier.get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @FunctionalInterface
    private interface TaskSupplier {
        Task get();
    }

    private ResponseEntity<Void> handleUpdateState(Long idTask, Long idState) {
        try {
            taskService.updateState(idTask, idState);
            return ResponseEntity.noContent().build();
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    
}
