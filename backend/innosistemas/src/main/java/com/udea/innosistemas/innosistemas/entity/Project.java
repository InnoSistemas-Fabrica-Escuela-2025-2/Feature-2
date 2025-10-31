package com.udea.innosistemas.innosistemas.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="projects", schema="project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="project_name", nullable = false )
    private String name;

    @Column(name="description", nullable = false )
    private String description;

    @Column(name="deadline", nullable = false )
    private Timestamp deadline;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonManagedReference
    private List<Objective> objectives = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="id_team", nullable=false)
    private Team team;

}
