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
    // Servicio para manejar tareas
    private TaskService taskService;

    @PostMapping("/save")
    // Endpoint para guardar una tarea
    public ResponseEntity<Task> saveTask(@RequestBody Task task) {
        try {
            Task saved = taskService.saveTask(task);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/listAll")
    // Endpoint para listar todas las tareas
    public ResponseEntity<List<Task>> listAllTasks() {
        return ResponseEntity.ok(taskService.listAllTasks());
    }

    @DeleteMapping("/delete/{id}")
    // Endpoint para eliminar una tarea por su id
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    // Endpoint para actualizar una tarea
    public ResponseEntity<Task> updatedTask(@RequestBody Task task) {
        try {
            Task updated = taskService.saveTask(task);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("updateState/{id_task}/{id_state}")
    // Endpoint para actualizar el estado de una tarea
    public ResponseEntity<Void> updateState(@PathVariable Long id_task, @PathVariable Long id_state) {
        taskService.updateState(id_task, id_state);
        return ResponseEntity.noContent().build();
    }
    
}
