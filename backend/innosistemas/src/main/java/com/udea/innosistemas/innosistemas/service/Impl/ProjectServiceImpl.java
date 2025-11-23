package com.udea.innosistemas.innosistemas.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.Project;
import com.udea.innosistemas.innosistemas.repository.ProjectRepository;
import com.udea.innosistemas.innosistemas.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService{

    @Autowired
    // Repositorio para manejar proyectos
    private ProjectRepository projectRepository;

    @Override
    //Guardar un proyecto en la base de datos
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    //Obtener todos los proyectos
    public List<Project> listAllProjects(){
        return projectRepository.findAll();
    }

    @Override
    //Obtener los ids de los proyectos de un estudiante por su id
    public List<Long> getProjectsById(Long student_id) {
        try{
            return projectRepository.getProjectsById(student_id);
        } catch (Exception e){
            throw new UnsupportedOperationException("El estudiante no existe.");
        }
    }

    @Override
    //Obtener todos los proyectos de un estudiante por su id
    public List<Project> listAllById(Long student_id) {
        try{
            List<Long> id_projects = getProjectsById(student_id);
            return projectRepository.findAllByIdIn(id_projects);
        } catch (Exception e){
            throw new UnsupportedOperationException("No fue posible mostrar todos los projectos.");

        }
    }  
    
}
