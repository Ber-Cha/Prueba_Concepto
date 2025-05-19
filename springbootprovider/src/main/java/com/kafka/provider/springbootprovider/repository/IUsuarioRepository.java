package com.kafka.provider.springbootprovider.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kafka.provider.springbootprovider.Entity.Usuario;
import com.kafka.provider.springbootprovider.Entity.UsuarioPK;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, UsuarioPK> {

    @Modifying
    @Query("UPDATE Usuario u SET u.password = :password WHERE u.id.login = :login AND u.id.idpersona = :idPersona")
    int updatePassword(@Param("login") String login,
            @Param("idPersona") Integer idPersona,
            @Param("password") String password);

    Usuario findByApiKey(String apiKey);

    @Query("SELECT u FROM Usuario u WHERE u.id.login = :login AND u.password = :password")
    Optional<Usuario> findByLoginAndPassword(@Param("login") String login, @Param("password") String password);

    @Query("SELECT u FROM Usuario u WHERE u.id.login = :login")
    Optional<Usuario> findByLogin(@Param("login") String login);

    @Query("SELECT u FROM Usuario u WHERE u.id.login = :login AND u.apiKey = :apiKey")
    Optional<Usuario> findByLoginAndApiKey(@Param("login") String login, @Param("apiKey") String apiKey);

    @Transactional
    @Modifying
    @Query("DELETE FROM Usuario u WHERE u.persona.id = :idpersona")
    void deleteByIdpersona(@Param("idpersona") Integer idpersona);

    @Query("SELECT u FROM Usuario u WHERE u.persona.id = :idpersona")
    Optional<Usuario> findByPersonaId(@Param("idpersona") Integer idpersona);

}
