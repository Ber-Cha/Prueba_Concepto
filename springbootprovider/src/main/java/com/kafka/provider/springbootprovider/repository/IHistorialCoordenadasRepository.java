package com.kafka.provider.springbootprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kafka.provider.springbootprovider.Entity.HistorialCoordenadas;

import java.util.List;

public interface IHistorialCoordenadasRepository extends JpaRepository<HistorialCoordenadas, Integer> {

    @Query("SELECT h FROM HistorialCoordenadas h WHERE h.persona.id = :personaId ORDER BY h.fechaRegistro DESC")
    List<HistorialCoordenadas> findByPersonaId(@Param("personaId") int personaId);
}

