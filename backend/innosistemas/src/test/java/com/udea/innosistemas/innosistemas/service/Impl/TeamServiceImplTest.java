package com.udea.innosistemas.innosistemas.service.Impl;

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

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @Test
    void nameTeam_returnsTeamName() {
        when(teamRepository.findNameByIdStudent(9L)).thenReturn("Equipo Gamma");

        String result = teamService.nameTeam(9L);

        assertEquals("Equipo Gamma", result);
        verify(teamRepository).findNameByIdStudent(9L);
    }

    @Test
    void nameTeam_wrapsRepositoryException() {
        when(teamRepository.findNameByIdStudent(3L)).thenThrow(new RuntimeException("fallo"));

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> teamService.nameTeam(3L));

        assertEquals("No fue posible encontrar el equipo.", exception.getMessage());
    }

    @Test
    void getStudentsNameById_returnsMembers() {
        when(teamRepository.findNameByIdStudent(4L)).thenReturn("Equipo Delta");
        List<String> members = List.of("Ana", "Luis");
        when(teamRepository.getStudentsNameById("Equipo Delta")).thenReturn(members);

        List<String> result = teamService.getStudentsNameById(4L);

        assertSame(members, result);
        verify(teamRepository).getStudentsNameById("Equipo Delta");
    }

    @Test
    void getStudentsNameById_wrapsErrorsFromRepository() {
        when(teamRepository.findNameByIdStudent(11L)).thenReturn("Equipo Beta");
        when(teamRepository.getStudentsNameById("Equipo Beta")).thenThrow(new RuntimeException("fallo"));

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> teamService.getStudentsNameById(11L));

        assertEquals("No existe el estudiante.", exception.getMessage());
    }
}
