package com.udea.innosistemas.innosistemas.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.udea.innosistemas.innosistemas.repository.TeamRepository;
import com.udea.innosistemas.innosistemas.service.Impl.TeamServiceImpl;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    private static final String REPOSITORY_FAILURE = "fallo";
    private static final String TEAM_NOT_FOUND_MESSAGE = "No fue posible encontrar el equipo.";
    private static final String STUDENT_NOT_FOUND_MESSAGE = "No existe el estudiante.";
    private static final String TEAM_DELTA_NAME = "Equipo Delta";

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @Test
    void nameTeamReturnsTeamName() {
        when(teamRepository.findNameByIdStudent(9L)).thenReturn("Equipo Gamma");

        String result = teamService.nameTeam(9L);

        assertEquals("Equipo Gamma", result);
        verify(teamRepository).findNameByIdStudent(9L);
    }

    @Test
    void nameTeamWrapsRepositoryException() {
    when(teamRepository.findNameByIdStudent(3L)).thenThrow(new RuntimeException(REPOSITORY_FAILURE));

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> teamService.nameTeam(3L));

    assertEquals(TEAM_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    void getStudentsNameByIdReturnsMembers() {
    when(teamRepository.findNameByIdStudent(4L)).thenReturn(TEAM_DELTA_NAME);
        List<String> members = List.of("Ana", "Luis");
    when(teamRepository.getStudentsNameById(TEAM_DELTA_NAME)).thenReturn(members);

        List<String> result = teamService.getStudentsNameById(4L);

        assertSame(members, result);
    verify(teamRepository).getStudentsNameById(TEAM_DELTA_NAME);
    }

    @Test
    void getStudentsNameByIdWrapsErrorsFromRepository() {
        when(teamRepository.findNameByIdStudent(11L)).thenReturn("Equipo Beta");
    when(teamRepository.getStudentsNameById("Equipo Beta")).thenThrow(new RuntimeException(REPOSITORY_FAILURE));

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> teamService.getStudentsNameById(11L));

    assertEquals(STUDENT_NOT_FOUND_MESSAGE, exception.getMessage());
    }
}
