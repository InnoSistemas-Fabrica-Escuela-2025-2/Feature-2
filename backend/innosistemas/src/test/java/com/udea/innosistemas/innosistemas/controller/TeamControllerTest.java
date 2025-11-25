package com.udea.innosistemas.innosistemas.controller;

import com.udea.innosistemas.innosistemas.service.TeamService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TeamControllerTest {
    @Mock
    TeamService teamService;
    @InjectMocks
    TeamController teamController;



    @Test
    void testGetStudentsNameById() {
        when(teamService.getStudentsNameById(anyLong())).thenReturn(List.of("getStudentsNameByIdResponse"));

        ResponseEntity<List<String>> result = teamController.getStudentsNameById(1L);
        Assertions.assertEquals(ResponseEntity.ok(List.of("getStudentsNameByIdResponse")), result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme