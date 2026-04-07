package com.portfolio.club_manager.services;

import com.portfolio.club_manager.entities.Persona;
import com.portfolio.club_manager.repositories.PersonaRepository;
import com.portfolio.club_manager.repositories.SocioRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final SocioRepository socioRepository;

    public PersonaService(PersonaRepository personaRepository, SocioRepository socioRepository) {
        this.personaRepository = personaRepository;
        this.socioRepository = socioRepository;
    }

    public Persona registrarPersona(Persona persona) {

        if (persona.getApynom() == null || persona.getDni() == null) {
            throw new IllegalArgumentException("Nombre, apellido y DNI son obligatorios");
        }

        return personaRepository.save(persona);
    }

    public List<Persona> obtenerTodasLasPersonas() {
        return personaRepository.findAll();
    }

    public List<Persona> listarDeudores() {
        return personaRepository.findByDeudaGreaterThan(0);
    }

    public Boolean esSocioActivo(Integer id) {

        personaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));

        return socioRepository.existsByPersonaId(id);
    }

    public Persona buscarPorId(Integer id) {
        return personaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));
    }

    public Persona guardar(Persona persona) {
        return personaRepository.save(persona);
    }
}
