package com.udea.innosistemas.innosistemas.service;


import java.time.LocalDate;
import java.util.List;


import com.udea.innosistemas.innosistemas.entity.Task;

public interface TaskService {

    public void deleteTask(long id);
    public Task saveTask(Task task);
    public List<Task> listAllTasks();
    public void updateState(Long id_task, Long id_state);
    public List<Task> findByDate(LocalDate deadline);
    public void sendNotification();
}
