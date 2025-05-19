package com.kafka.provider.springbootprovider.service_impl;

import java.util.List;
import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kafka.provider.springbootprovider.Entity.Coordenadas;
import com.kafka.provider.springbootprovider.repository.ICoordenadasRepository;
import com.kafka.provider.springbootprovider.service.CoordenadaService;

@Service("CoordenadasService")
public class CoordenadaServiceImpl implements CoordenadaService {

    @Autowired
    @Qualifier("ICoordenadasRepository")
    private ICoordenadasRepository coordenadasRepository;

    private static final Logger logger = LogManager.getLogger(PersonaServiceImpl.class);

    @Override
    public List<Coordenadas> consultarAllCoordenadas(Pageable pageable) {
        return coordenadasRepository.findAll(pageable).getContent();

    }

}
