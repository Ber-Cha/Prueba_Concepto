package com.kafka.provider.springbootprovider.service_impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kafka.provider.springbootprovider.Entity.Persona;
import com.kafka.provider.springbootprovider.Entity.Usuario;
import com.kafka.provider.springbootprovider.dto.PersonasDto;
import com.kafka.provider.springbootprovider.repository.IPersonaRepository;
import com.kafka.provider.springbootprovider.repository.IUsuarioRepository;
import com.kafka.provider.springbootprovider.service.IPersonaService;

@Service
public class PersonaServiceImpl implements IPersonaService {

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public Optional<PersonasDto> get(Integer id) {
        return personaRepository.findById(id)
                .map(this::convertToDto); // Si no se encuentra, devuelve Optional.empty()
    }

    @Override
    public PersonasDto update(PersonasDto personaDto) {
        Persona personaBD = personaRepository.findById(personaDto.getId())
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        boolean nombreCambiado = false;
        boolean apellidoCambiado = false;

        if (personaDto.getIdentificacion() != 0) {
            personaBD.setIdentificacion(personaDto.getIdentificacion());
        }

        if (personaDto.getPNombre() != null && !personaDto.getPNombre().equals(personaBD.getPNombre())) {
            personaBD.setPNombre(personaDto.getPNombre());
            nombreCambiado = true;
        }

        if (personaDto.getSNombre() != null) {
            personaBD.setSNombre(personaDto.getSNombre());
        }

        if (personaDto.getPApellido() != null && !personaDto.getPApellido().equals(personaBD.getPApellido())) {
            personaBD.setPApellido(personaDto.getPApellido());
            apellidoCambiado = true;
        }

        if (personaDto.getSApellido() != null) {
            personaBD.setSApellido(personaDto.getSApellido());
        }

        if (personaDto.getEmail() != null) {
            personaBD.setEmail(personaDto.getEmail());
        }

        if (personaDto.getFechanacimiento() != null) {

            personaBD.setFechanacimiento(personaDto.getFechanacimiento());

            LocalDate today = LocalDate.now();

            Period period = Period.between(personaDto.getFechanacimiento(), today);
            int edad = period.getYears();
            String edadClinica = period.getYears() + " años " + period.getMonths() + " meses "
                    + period.getDays()
                    + " días";
            personaBD.setEdad(edad);
            personaBD.setEdadclinica(edadClinica);
        }

        if (personaDto.getEdad() != 0) {
            personaBD.setEdad(personaDto.getEdad());
        }

        if (personaDto.getEdadclinica() != null) {
            personaBD.setEdadclinica(personaDto.getEdadclinica());
        }

        if (personaDto.getUbicacion() != null) {
            personaBD.setUbicacion(personaDto.getUbicacion());
        }

        Persona P_Actualizada = personaRepository.save(personaBD);

        return convertToDto(P_Actualizada);
    }

    @Override
    public void delete(Integer id) {
        // TODO Auto-generated method stub
        personaRepository.deleteById(id);
    }

    @Override
    public List<PersonasDto> findAll() {
        return personaRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonasDto> findByIdentificacion(Integer identificacion) {
        return personaRepository.findByIdentificacion(identificacion)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<PersonasDto> findByEdad(Integer edad) {
        return personaRepository.findByEdad(edad)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonasDto> findByPApellido(String pApellido) {
        return personaRepository.findBypApellido(pApellido)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonasDto> findByPNombre(String pNombre) {
        return personaRepository.findBypNombre(pNombre)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PersonasDto convertToDto(Persona persona) {
        return PersonasDto.builder()
                .id(persona.getId())
                .identificacion(persona.getIdentificacion())
                .pNombre(persona.getPNombre())
                .sNombre(persona.getSNombre())
                .pApellido(persona.getPApellido())
                .sApellido(persona.getSApellido())
                .email(persona.getEmail())
                .fechanacimiento(persona.getFechanacimiento())
                .edad(persona.getEdad())
                .edadclinica(persona.getEdadclinica())
                .Ubicacion(persona.getUbicacion())
                .build();
    }

}
