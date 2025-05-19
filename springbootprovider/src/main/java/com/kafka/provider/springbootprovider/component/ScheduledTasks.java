package com.kafka.provider.springbootprovider.component;

import com.kafka.provider.springbootprovider.APIs.GoogleMaps.Geocoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kafka.provider.springbootprovider.Entity.Coordenadas;
import com.kafka.provider.springbootprovider.Entity.Persona;
import com.kafka.provider.springbootprovider.repository.ICoordenadasRepository;
import com.kafka.provider.springbootprovider.repository.IPersonaRepository;

@Component
public class ScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private ICoordenadasRepository coordenadasRepository;

    @Scheduled(cron = "*/30 * * * * ?")
    public void scheduleTaskWithronExpression() {
        LOGGER.info("Cron Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            List<Persona> listPersonas = personaRepository.findAll();
            if (listPersonas != null) {
                if (listPersonas.size() > 0) {
                    Geocoder geocoder = new Geocoder();
                    Coordenadas coorXper = new Coordenadas();
                    for (Persona persona : listPersonas) {
                        String latLng = geocoder.getLatLng(persona.getUbicacion());
                        String[] coor = latLng.split(",");
                        LOGGER.info(latLng + " - {}", dateTimeFormatter.format(LocalDateTime.now()));

                        coorXper = coordenadasRepository.getCoordenadaXPersona(persona);

                        if (coorXper == null) {
                            coordenadasRepository.save(Coordenadas.builder()
                                    .persona(persona)
                                    .marca(persona.getPNombre())
                                    .latitud(Double.parseDouble(coor[0].toString()))
                                    .longitud(Double.parseDouble(coor[1].toString()))
                                    .build());
                        } else if (coorXper.getId() > 0) {
                            coordenadasRepository.save(Coordenadas.builder()
                                    .id(coorXper.getId())
                                    .persona(persona)
                                    .marca(persona.getPNombre())
                                    .latitud(Double.parseDouble(coor[0].toString()))
                                    .longitud(Double.parseDouble(coor[1].toString()))
                                    .build());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
    }
}
