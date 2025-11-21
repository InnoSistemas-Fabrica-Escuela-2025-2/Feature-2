package com.udea.innosistemas.innosistemas.service.Impl;

import java.sql.Timestamp;
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

    //Eliminar una tarea por su id
    @Override
    public void deleteTask(long id) {
        if(taskRepository.existsById(id)){
            taskRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("El usuario no existe.");
        }
    }

    //Guardar una tarea en la base de datos
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

    //Obtener todas las tareas
    @Override
    public List<Task> listAllTasks() {
        return taskRepository.findAll();
    }

    //Actualizar el estado de una tarea
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

    //Buscar tareas por fecha de vencimiento
    @Override
    public List<Task> findByDate(Timestamp deadline) {
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
        Timestamp deadline = Timestamp.from(java.time.Instant.now().plusSeconds(3 * 24 * 60 * 60)); 
        LocalDate localDate = deadline.toLocalDateTime().toLocalDate();
        try{
            List<Task> tasks = findByDate(deadline);

            if (tasks == null || tasks.isEmpty()) {
            System.out.print("No hay tareas");
            return; 
        }
            for (Task task:tasks){
                notificationProducerImpl.sendEmail(new EmailEvent(task.getResponsible_email(), "¡Acuérdate de realizar tu tarea!", 
                "La tarea " + task.getTitle() + "vence el día " + localDate.getDayOfWeek() + "" + 
                localDate.getDayOfMonth() + " de " + localDate.getMonth() + 
                "a las " + deadline.toLocalDateTime().getHour() + ":" + deadline.toLocalDateTime().getMinute() +
                " , la cual pertenece al proyecto "+
                task.getProject().getName()));
            } 
        }catch(Exception e){
            throw new UnsupportedOperationException("No es posible enviar la notificación: " + e.getMessage());
        }
    }

}
