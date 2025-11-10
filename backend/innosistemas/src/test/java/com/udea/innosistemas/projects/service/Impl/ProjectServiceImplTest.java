package com.udea.innosistemas.projects.service.Impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.udea.innosistemas.projects.entity.Project;
import com.udea.innosistemas.projects.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void saveProjectReturnsPersistedEntity() {
        Project project = new Project();
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.saveProject(project);

        assertSame(project, result);
        verify(projectRepository).save(project);
    }

    @Test
    void listAllProjectsReturnsRepositoryData() {
        Project project = new Project();
        List<Project> projects = Collections.singletonList(project);
        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.listAllProjects();

        assertSame(projects, result);
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectsByIdPropagatesRepositoryValues() {
        List<Long> projectIds = List.of(1L, 2L);
        when(projectRepository.getProjectsById(7L)).thenReturn(projectIds);

        List<Long> result = projectService.getProjectsById(7L);

        assertSame(projectIds, result);
        verify(projectRepository).getProjectsById(7L);
    }

    @Test
    void getProjectsByIdWrapsRepositoryErrors() {
        when(projectRepository.getProjectsById(anyLong())).thenThrow(new RuntimeException("DB failure"));

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> projectService.getProjectsById(5L));

        assertEquals("El estudiante no existe.", exception.getMessage());
    }

    @Test
    void listAllByIdReturnsProjectsForStudent() {
        List<Long> projectIds = List.of(3L, 8L);
        Project project = new Project();
        List<Project> projects = Collections.singletonList(project);

        when(projectRepository.getProjectsById(4L)).thenReturn(projectIds);
        when(projectRepository.findAllByIdIn(projectIds)).thenReturn(projects);

        List<Project> result = projectService.listAllById(4L);

        assertSame(projects, result);
        verify(projectRepository).findAllByIdIn(projectIds);
    }

    @Test
    void listAllByIdWrapsErrorsFromRepository() {
        List<Long> projectIds = List.of(10L);
        when(projectRepository.getProjectsById(12L)).thenReturn(projectIds);
        when(projectRepository.findAllByIdIn(projectIds)).thenThrow(new RuntimeException("query failed"));

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> projectService.listAllById(12L));

        assertEquals("No fue posible mostrar todos los projectos.", exception.getMessage());
    }
}
