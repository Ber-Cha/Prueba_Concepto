package com.kafka.provider.springbootprovider.service;

import com.kafka.provider.springbootprovider.Entity.HistorialCoordenadas;
import com.kafka.provider.springbootprovider.repository.IHistorialCoordenadasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialCoordenadaService {

    @Autowired
    private IHistorialCoordenadasRepository historialRepo;

    public List<HistorialCoordenadas> obtenerHistorialPorPersonaId(int personaId) {
        return historialRepo.findByPersonaId(personaId);
    }
}