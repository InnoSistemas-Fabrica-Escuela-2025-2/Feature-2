package com.udea.innosistemas.innosistemas.service.Impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.udea.innosistemas.innosistemas.service.NotificationProducer;
import com.udea.innosistemas.innosistemas.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private NotificationProducer notificationProducer;

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
    public List<Task> findByDate(Timestamp today, Timestamp limit) {
        try{
            return taskRepository.findByDeadlineBetween(today, limit);
        } catch(Exception e){
            throw new UnsupportedOperationException("No fue posible listar las tareas." + e.getMessage());
        }
    }

    //Método que se ejecuta todos los días a las 10am para verificar que tareas están próximas a vencer
    @Override
    @Scheduled(cron = "0 0 10 * * *")
    public void sendNotification() {
        Timestamp today = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp limit = Timestamp.valueOf(LocalDate.now().plusDays(3).atStartOfDay());
        LocalDate localDate = limit.toLocalDateTime().toLocalDate();
        System.out.println("Hoy es: " + today + " y el límite es: " + limit);
        try{
            List<Task> tasks = findByDate(today, limit);

            if (tasks == null || tasks.isEmpty()) {
            System.out.print("No hay tareas");
            return; 
        }
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM"); 
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            for (Task task:tasks){
                String fecha = localDate.format(dateFormatter);
                String hora = limit.toLocalDateTime().format(timeFormatter);

                String mensaje = "Hola, \n\nLa tarea " + task.getTitle() +
                     " vence el día " + fecha +
                     " a las " + hora +
                     ", la cual pertenece al proyecto " + task.getProject().getName() + "." + 
                     "\n\n¡No dejes todo para el final!";
                
                notificationProducer.sendEmail(new EmailEvent(
                    task.getResponsible_email(),
                    "¡Acuérdate de realizar tu tarea!",
                    mensaje
                ));
            } 
        }catch(Exception e){
            throw new UnsupportedOperationException("No es posible enviar la notificación: " + e.getMessage());
        }
    }

}
