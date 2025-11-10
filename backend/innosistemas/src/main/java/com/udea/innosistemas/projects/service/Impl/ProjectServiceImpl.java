package com.udea.innosistemas.projects.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.projects.entity.Project;
import com.udea.innosistemas.projects.repository.ProjectRepository;
import com.udea.innosistemas.projects.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService{


    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> listAllProjects(){
        return projectRepository.findAll();
    }

    @Override
    public List<Long> getProjectsById(Long student_id) {
        return handleGetProjectsById(student_id);
    }

    @Override
    public List<Project> listAllById(Long student_id) {
        return handleListAllById(student_id);
    }

    @Override
    public List<Project> listAllByTeamId(Long team_id) {
        return handleListAllByTeamId(team_id);
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
