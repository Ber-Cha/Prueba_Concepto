package com.kafka.provider.springbootprovider.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kafka.provider.springbootprovider.Entity.Coordenadas;
import com.kafka.provider.springbootprovider.Entity.Persona;

public interface ICoordenadasRepository extends JpaRepository<Coordenadas, Integer> {

    Page<Coordenadas> findAll(Pageable pageable);

    @Query("SELECT coord FROM Coordenadas coord WHERE coord.persona = :persona")
    Coordenadas getCoordenadaXPersona(@Param("persona") Persona persona);

    @Query("SELECT coord FROM Coordenadas coord WHERE coord.persona.id = :personaId")
    List<Coordenadas> getHistorialCoordenadasPorPersonaId(@Param("personaId") int personaId);
}
