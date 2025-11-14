package com.udea.innosistemas.innosistemas.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.Project;
import com.udea.innosistemas.innosistemas.repository.ProjectRepository;
import com.udea.innosistemas.innosistemas.service.ProjectService;


@Service
public class ProjectServiceImpl implements ProjectService{


    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project saveProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("El proyecto no puede ser nulo.");
        }
        try {
            return projectRepository.save(project);
        } catch (Exception e) {
            throw new UnsupportedOperationException("No fue posible guardar el proyecto.", e);
        }
    }

    @Override
    public List<Project> listAllProjects(){
        return projectRepository.findAll();
    }

    @Override
    public List<Long> getProjectsById(Long studentId) {
        return handleGetProjectsById(studentId);
    }

    @Override
    public List<Project> listAllById(Long studentId) {
        return handleListAllById(studentId);
    }

    @Override
    public List<Project> listAllByTeamId(Long teamId) {
        return handleListAllByTeamId(teamId);
    }

    private List<Long> handleGetProjectsById(Long studentId) {
        try {
            return projectRepository.getProjectsById(studentId);
        } catch (Exception e) {
            throw new UnsupportedOperationException("El estudiante no existe.");
        }
    }

    private List<Project> handleListAllById(Long studentId) {
        try {
            List<Long> idProjects = handleGetProjectsById(studentId);
            return projectRepository.findAllByIdIn(idProjects);
        } catch (Exception e) {
            throw new UnsupportedOperationException("No fue posible mostrar todos los projectos.");
        }
    }

    private List<Project> handleListAllByTeamId(Long teamId) {
        try {
            return projectRepository.findAllByTeamId(teamId);
        } catch (Exception e) {
            throw new UnsupportedOperationException("No fue posible mostrar los proyectos del equipo.");
        }
    }
    
    
}
