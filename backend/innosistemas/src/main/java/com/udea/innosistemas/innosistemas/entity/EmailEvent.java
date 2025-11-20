package com.udea.innosistemas.innosistemas.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailEvent {

    private String to;
    private String subject;
    private String body;
}
