package com.udea.innosistemas.innosistemas.service.Impl;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.EmailEvent;
import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.entity.Task;
import com.udea.innosistemas.innosistemas.repository.StateRepository;
import com.udea.innosistemas.innosistemas.repository.TaskRepository;
import com.udea.innosistemas.innosistemas.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private NotificationProducerImpl notificationProducerImpl;

    @Override
    public void deleteTask(long id) {
        if(taskRepository.existsById(id)){
            taskRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("El usuario no existe.");
        }
    }

    @Override
    public Task saveTask(Task task) {
        try {
            if (task.getState() == null){
                State newState = new State();
                newState.setId(3L);
                task.setState(newState);
            }
            return taskRepository.save(task);
        } catch (Exception e){
            throw new NoSuchElementException("No fue posible guardar la tarea.");
        }
    }

    @Override
    public List<Task> listAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void updateState(Long id_task, Long id_state) {
        if (!taskRepository.findById(id_task).isPresent()){
            throw new UnsupportedOperationException("La tarea no existe.");
        }

        Task task = taskRepository.findById(id_task).get();
        State state = stateRepository.findById(id_state).get();
        task.setState(state);
        taskRepository.save(task);
    }

    @Override
    public List<Task> findByDate(LocalDate deadline) {
        try{
            return taskRepository.findByDeadline(deadline);
        } catch(Exception e){
            throw new UnsupportedOperationException("No fue posible listar las tareas." + e.getMessage());
        }
        
    }

    //Método que se ejecuta todos los días a las 10am para verificar que tareas están próximas a vencer
    @Override
    @Scheduled(cron = "0 * * * * *")
    public void sendNotification() {
        LocalDate deadline = LocalDate.now().plusDays(3);
        try{
            List<Task> tasks = findByDate(deadline);

            if (tasks == null || tasks.isEmpty()) {
            System.out.print("No hay tareas");
            return; 
        }
            for (Task task:tasks){
                notificationProducerImpl.sendEmail(new EmailEvent(task.getResponsible(), "¡Acuérdate de realizar tu tarea!", 
                "La tarea " + task.getTitle() + "vence el día " + deadline.getDayOfWeek() + "" + 
                deadline.getDayOfMonth() + " de " + deadline.getMonth() + " , la cual pertenece al proyecto "+
                task.getProject().getName()));
                
            } 
        }catch(Exception e){
            throw new UnsupportedOperationException("No es posible enviar la notificación: " + e.getMessage());
        }
    }


        

}
