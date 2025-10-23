package com.udea.innosistemas.innosistemas.entity;

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
@Table(name="objetivos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Objective {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name="descripcion")
    private String description;

    @ManyToOne
    @JoinColumn(name="id_proyecto", nullable=false)
    @JsonBackReference
    private Project project;
}
