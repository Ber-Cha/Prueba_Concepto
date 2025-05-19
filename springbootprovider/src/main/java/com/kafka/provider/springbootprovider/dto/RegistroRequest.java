package com.kafka.provider.springbootprovider.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroRequest {
    private int identificacion;
    private String pNombre;
    private String sNombre;
    private String pApellido;
    private String sApellido;
    private String email;
    private LocalDate fechanacimiento;
    private String ubicacion;
} 