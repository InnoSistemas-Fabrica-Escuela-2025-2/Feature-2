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
    private ProjectRepository projectRepository;


    //Guardar un proyecto en la base de datos
    @Override
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    //Obtener todos los proyectos
    @Override
    public List<Project> listAllProjects(){
        return projectRepository.findAll();
    }

    //Obtener los ids de los proyectos de un estudiante por su id
    @Override
    public List<Long> getProjectsById(Long student_id) {
        try{
            return projectRepository.getProjectsById(student_id);
        } catch (Exception e){
            throw new UnsupportedOperationException("El estudiante no existe.");
        }
    }

    //Obtener todos los proyectos de un estudiante por su id
    @Override
    public List<Project> listAllById(Long student_id) {
        try{
            List<Long> id_projects = getProjectsById(student_id);
            return projectRepository.findAllByIdIn(id_projects);
        } catch (Exception e){
            throw new UnsupportedOperationException("No fue posible mostrar todos los projectos.");

        }
    }  
    
}
