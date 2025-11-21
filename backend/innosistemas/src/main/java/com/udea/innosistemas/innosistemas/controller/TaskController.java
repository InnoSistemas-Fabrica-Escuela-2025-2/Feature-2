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
        try {
            Task saved = taskService.saveTask(task);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
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
        try {
            Task updated = taskService.saveTask(task);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("updateState/{id_task}/{id_state}")
    public ResponseEntity<Void> updateState(@PathVariable Long id_task, @PathVariable Long id_state) {
        taskService.updateState(id_task, id_state);
        return ResponseEntity.noContent().build();
    }
    
}
