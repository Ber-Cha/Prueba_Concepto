package com.kafka.provider.springbootprovider.service;

import com.kafka.provider.springbootprovider.Entity.Usuario;


public interface IUsuarioService {
    Usuario save(Usuario usuario);

    void delete(Integer idpersona);

    Usuario getByPersonaId(Integer id);

    Boolean updatePassword(String login, Integer idPersona, String nuevaPassword);

    Usuario findByApiKey(String apikey);

    Usuario findByLogin(String login);
}
