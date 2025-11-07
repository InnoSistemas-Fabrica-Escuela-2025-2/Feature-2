package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.innosistemas.innosistemas.entity.Task;
import com.udea.innosistemas.innosistemas.service.TaskService;

@RestController
@RequestMapping("/project/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

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
    
    @PutMapping("updateState/{id_task}/{id_state}")
    public ResponseEntity<Void> updateState(@PathVariable Long id_task, @PathVariable Long id_state) {
        return handleUpdateState(id_task, id_state);
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
