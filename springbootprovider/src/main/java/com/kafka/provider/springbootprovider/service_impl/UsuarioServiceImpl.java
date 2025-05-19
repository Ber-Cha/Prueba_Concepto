package com.kafka.provider.springbootprovider.service_impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kafka.provider.springbootprovider.Entity.Persona;
import com.kafka.provider.springbootprovider.Entity.Usuario;
import com.kafka.provider.springbootprovider.Entity.UsuarioPK;
import com.kafka.provider.springbootprovider.repository.IPersonaRepository;
import com.kafka.provider.springbootprovider.repository.IUsuarioRepository;
import com.kafka.provider.springbootprovider.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IPersonaRepository personaRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Override
    public Usuario save(Usuario usuario) {
        Persona persona = usuario.getPersona();

        // Validar que la persona tiene un apellido válido antes de generar el login
        if (persona.getPApellido() == null || persona.getPApellido().isEmpty()) {
            throw new IllegalArgumentException("El primer apellido de la persona no puede ser nulo o vacío.");
        }

        // Generar el login
        String login = persona.getPNombre() + persona.getPApellido().charAt(0) + persona.getId();
        usuario.getId().setLogin(login);

        // Generar password
        usuario.setPassword(generarPasswordInicial());

        // Generar una API key única para el usuario
        usuario.setApiKey(ApiKeyGenerator.generateApiKey());
        return usuarioRepository.save(usuario);

    }

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Boolean updatePassword(String login, Integer idPersona, String nuevaPassword) {
        LOGGER.info("Buscando usuario con login: " + login + " y idPersona: " + idPersona);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(new UsuarioPK(login, idPersona));
        if (usuarioOpt.isPresent()) {
            LOGGER.info("Usuario encontrado: " + usuarioOpt.get().getId());
            Usuario usuario = usuarioOpt.get();
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            usuarioRepository.save(usuario);
            return true;
        }
        LOGGER.warn("Usuario no encontrado con login: " + login + " y idPersona: " + idPersona);
        return false;

    }

    @Override
    public void delete(Integer idpersona) {
        usuarioRepository.deleteByIdpersona(idpersona);
    }

    @Override
    public Usuario getByPersonaId(Integer idpersona) {
        return usuarioRepository.findByPersonaId(idpersona).orElse(null);
    }

    @Override
    public Usuario findByApiKey(String apiKey) {
        return usuarioRepository.findByApiKey(apiKey);
    }

    @Override
    public Usuario findByLogin(String login) {
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con login: " + login));
    }

    // Método privado para generar password inicial
    private String generarPasswordInicial() {
        // Generar un password aleatorio de 8 caracteres
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * caracteres.length());
            password.append(caracteres.charAt(index));
        }
        return password.toString();
    }
}
