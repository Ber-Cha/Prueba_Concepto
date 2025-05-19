package com.kafka.provider.springbootprovider.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.kafka.provider.springbootprovider.Entity.Coordenadas;

public interface CoordenadaService {

    public List<Coordenadas> consultarAllCoordenadas(Pageable pageable);

}
