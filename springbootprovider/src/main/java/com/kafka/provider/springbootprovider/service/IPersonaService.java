package com.kafka.provider.springbootprovider.service;

import java.util.List;
import java.util.Optional;

import com.kafka.provider.springbootprovider.Entity.Persona;
import com.kafka.provider.springbootprovider.dto.PersonasDto;

public interface IPersonaService {

    Optional<PersonasDto> get(Integer id);

    PersonasDto update(PersonasDto personaDto);

    void delete(Integer id);

    List<PersonasDto> findAll();

    List<PersonasDto> findByIdentificacion(Integer identificacion);

    List<PersonasDto> findByEdad(Integer edad);

    List<PersonasDto> findByPApellido(String pApellido);

    List<PersonasDto> findByPNombre(String PNombre);
}
