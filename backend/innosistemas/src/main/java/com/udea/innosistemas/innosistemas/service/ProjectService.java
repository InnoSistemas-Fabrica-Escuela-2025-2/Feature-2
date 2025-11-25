package com.udea.innosistemas.innosistemas.service;

import java.util.List;

import com.udea.innosistemas.innosistemas.entity.Project;

public interface ProjectService {
    
    public Project saveProject(Project project);
    public List<Project> listAllProjects();
    public Project getProjectById(Long projectId);
    public List<Long> getProjectsById(Long studentId);
    public List<Project> listAllById(Long studentId);
    public List<Project> listAllByTeamId(Long teamId);
}
