package com.innosistemas.notifications.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.innosistemas.notifications.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    @Query(value = "SELECT * FROM notification.getidstudentbyemail(:email)", nativeQuery=true)
    Long findIdByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM notification.getnotificationbyidstudent(:id_responsible)", nativeQuery=true)
    List<Notification> findByIdStudent(@Param("id_responsible") Long id_responsible);
}
