package com.udea.innosistemas.innosistemas.service.impl;

import com.udea.innosistemas.innosistemas.entity.Team;
import com.udea.innosistemas.innosistemas.repository.TeamRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {
    @Mock
    TeamRepository teamRepository;
    @InjectMocks
    TeamServiceImpl teamServiceImpl;

    @Test
    void testGetStudentsNameById() {
        when(teamRepository.findNameByIdStudent(1L))
                .thenReturn("TeamA");
        when(teamRepository.getStudentsNameById("TeamA"))
                .thenReturn(List.of("Juan", "Maria"));
        List<String> result = teamServiceImpl.getStudentsNameById(1L);
        Assertions.assertEquals(List.of("Juan", "Maria"), result);
    }

    @Test
    void testListAllTeams() {
        Team team = new Team(1L, "TeamA");
        when(teamRepository.findAll())
                .thenReturn(List.of(team));
        List<Team> result = teamServiceImpl.listAllTeams();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("TeamA", result.get(0).getName());
    }


    @Test
    void testGetTeamIdByStudent() {
        when(teamRepository.findNameByIdStudent(1L))
                .thenReturn("TeamA");
        Team team = new Team(10L, "TeamA");
        when(teamRepository.findAll())
                .thenReturn(List.of(team));
        Long result = teamServiceImpl.getTeamIdByStudent(1L);
        Assertions.assertEquals(10L, result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme