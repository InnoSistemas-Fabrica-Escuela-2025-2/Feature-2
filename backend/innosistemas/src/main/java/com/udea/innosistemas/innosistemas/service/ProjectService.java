package com.udea.innosistemas.innosistemas.service;

import java.util.List;

import com.udea.innosistemas.innosistemas.entity.Project;

public interface ProjectService {
    
    public Project saveProject(Project project);
    public List<Project> listAllProjects();
}
