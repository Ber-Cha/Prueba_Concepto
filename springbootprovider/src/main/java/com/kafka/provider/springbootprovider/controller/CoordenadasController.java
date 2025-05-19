package com.kafka.provider.springbootprovider.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.provider.springbootprovider.Entity.Coordenadas;
import com.kafka.provider.springbootprovider.Entity.HistorialCoordenadas;
import com.kafka.provider.springbootprovider.service.CoordenadaService;
import com.kafka.provider.springbootprovider.service.HistorialCoordenadaService;
import com.kafka.provider.springbootprovider.service_impl.CoordenadaServiceImpl;

import io.swagger.v3.oas.annotations.Hidden;


// Controlador para la gestión de coordenadas y su historial

@RestController
@Hidden
@RequestMapping("/ubicacion")
public class CoordenadasController {

    @Autowired
    private CoordenadaService coordenadaService;

    @Autowired
    private HistorialCoordenadaService historialService;

  
    @CrossOrigin(origins = { "http://localhost" })
    @GetMapping("/coordenadas")
    public List<Coordenadas> consultarAllCoordenadas(Pageable pageable) {
        return coordenadaService.consultarAllCoordenadas(pageable);
    }

    @CrossOrigin(origins = { "http://localhost" })
    @GetMapping("/coordenadas/historial/{personaId}")
    public List<HistorialCoordenadas> obtenerHistorialPorPersona(@PathVariable int personaId) {
        return historialService.obtenerHistorialPorPersonaId(personaId);
    }
}
