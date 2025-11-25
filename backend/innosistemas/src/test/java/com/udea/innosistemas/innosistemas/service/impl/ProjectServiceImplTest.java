package com.udea.innosistemas.innosistemas.service.impl;

import com.udea.innosistemas.innosistemas.entity.*;
import com.udea.innosistemas.innosistemas.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    ProjectRepository projectRepository;
    @InjectMocks
    ProjectServiceImpl projectServiceImpl;
    Project project = new Project();
    @Test
    void testSaveProject() {
        Project project = new Project();
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectServiceImpl.saveProject(project);

        assertEquals(project, result);
        verify(projectRepository).save(project);
    }

    @Test
    void testListAllProjects() {
        List<Project> projects = List.of(new Project());
        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectServiceImpl.listAllProjects();

        assertEquals(projects, result);
        verify(projectRepository).findAll();
    }

    @Test
    void testGetProjectsById() {
        when(projectRepository.getProjectsById(anyLong())).thenReturn(List.of(1L));

        List<Long> result = projectServiceImpl.getProjectsById(1L);
        assertEquals(List.of(1L), result);
    }

    @Test
    void testListAllById() {
        when(projectRepository.getProjectsById(1L))
                .thenReturn(List.of(100L));
        List<Project> projects = List.of(new Project());
        when(projectRepository.findAllByIdIn(List.of(100L)))
                .thenReturn(projects);
        List<Project> result = projectServiceImpl.listAllById(1L);
        assertEquals(projects, result);
        verify(projectRepository).getProjectsById(1L);
        verify(projectRepository).findAllByIdIn(List.of(100L));
    }

    @Test
    void testListAllByTeamId() {
        List<Project> projects = List.of(new Project());
        when(projectRepository.findAllByTeamId(5L)).thenReturn(projects);
        List<Project> result = projectServiceImpl.listAllByTeamId(5L);
        assertEquals(projects, result);
        verify(projectRepository).findAllByTeamId(5L);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme