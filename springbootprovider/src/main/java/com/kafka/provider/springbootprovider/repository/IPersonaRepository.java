package com.kafka.provider.springbootprovider.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kafka.provider.springbootprovider.Entity.Persona;

@Repository
public interface IPersonaRepository extends JpaRepository<Persona, Integer> {

    List<Persona> findByIdentificacion(Integer identificacion);

    List<Persona> findByEdad(Integer edad);

    List<Persona> findBypApellido(String pApellido);

    List<Persona> findBypNombre(String pNombre);

    Optional<Persona> findByEmail(String email);

    @Query("SELECT p FROM Persona p WHERE p.usuario.id.login = :login")
    Optional<Persona> findByUsuarioLogin(@Param("login") String login);

}