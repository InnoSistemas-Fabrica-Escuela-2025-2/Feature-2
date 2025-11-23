package com.udea.innosistemas.innosistemas.service.Impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
    // Repositorio para manejar tareas
    private TaskRepository taskRepository;

    @Autowired
    // Repositorio para manejar estados
    private StateRepository stateRepository;

    @Autowired
    // Servicio para manejar notificaciones
    private NotificationProducer notificationProducer;

    @Override
    //Eliminar una tarea por su id
    public void deleteTask(long id) {
        if(taskRepository.existsById(id)){
            taskRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("El usuario no existe.");
        }
    }

    @Override
    //Guardar una tarea en la base de datos
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
    //Obtener todas las tareas
    public List<Task> listAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    //Actualizar el estado de una tarea
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
    //Buscar tareas por fecha de vencimiento
    public List<Task> findByDate(Timestamp today, Timestamp limit) {
        try{
            return taskRepository.findByDeadlineBetween(today, limit);
        } catch(Exception e){
            throw new UnsupportedOperationException("No fue posible listar las tareas." + e.getMessage());
        }
    }

    @Override
    @Scheduled(cron = "0 */2 * * * *")
    public void sendNotification() {
        LocalDateTime threeDaysFromNowStart = LocalDate.now().plusDays(3).atStartOfDay();  // 00:00 del día +3
        LocalDateTime threeDaysFromNowEnd = threeDaysFromNowStart.plusDays(1).minusNanos(1);  // 23:59:59 del día +3
        Timestamp start = Timestamp.valueOf(threeDaysFromNowStart);
        Timestamp end = Timestamp.valueOf(threeDaysFromNowEnd);
        System.out.println("Buscando tareas que venzan exactamente entre: " + start + " y " + end);
        try{
            List<Task> tasks = findByDate(start, end);

            if (tasks == null || tasks.isEmpty()) {
            System.out.print("No hay tareas");
            return; 
            }
            
            Locale locale = new Locale("es", "ES");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", locale); 
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", locale);

            for (Task task:tasks){
                String fecha = threeDaysFromNowStart.toLocalDate().format(dateFormatter);
                String hora = threeDaysFromNowStart.format(timeFormatter); 

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
