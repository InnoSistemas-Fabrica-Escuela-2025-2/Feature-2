package com.innosistemas.authenticator.entity;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "login_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "last_attempt")
    private Timestamp lastAttempt;

    @Column(name = "block_until")
    private Timestamp blockUntil;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id", unique = true)
    private Person person;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
