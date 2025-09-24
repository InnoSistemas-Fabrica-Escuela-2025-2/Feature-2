package com.udea.innosistemas.innosistemas.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="title", nullable = false )
    private String title;

    @Column(name="description", nullable = false )
    private String description;

    @Column(name="deadline", nullable = false )
    private Timestamp deadline;

    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    @JsonBackReference
    private Project project;

}
