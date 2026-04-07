package com.portfolio.club_manager.services;

import com.portfolio.club_manager.entities.Persona;
import com.portfolio.club_manager.entities.Socio;
import com.portfolio.club_manager.repositories.PersonaRepository;
import com.portfolio.club_manager.repositories.SocioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SocioService {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    
    public SocioService(SocioRepository socioRepository, PersonaRepository personaRepository) {
        this.socioRepository = socioRepository;
        this.personaRepository = personaRepository;
    }

    public Socio registrarSocio(Socio socio) {
        if (socio.getPersona() == null || socio.getPersona().getId() == null) {
            throw new IllegalArgumentException("El socio debe estar vinculado a una Persona existente.");
        }

        
        Persona personaReal = personaRepository.findById(socio.getPersona().getId())
                .orElseThrow(() -> new IllegalArgumentException("La persona indicada no existe en el sistema."));

        
        socio.setPersona(personaReal);

        
        if (socio.getFechaAlta() == null) {
            socio.setFechaAlta(LocalDate.now());
        }

        
        return socioRepository.save(socio);
    }

    public List<Socio> obtenerTodosLosSocios() {
        return socioRepository.findAll();
    }

    @Transactional
    public Socio promocionarASocio(Integer personaId) {
        
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new IllegalArgumentException("La persona no existe"));

        
        if (socioRepository.existsByPersonaId(personaId)) {
            throw new IllegalArgumentException("Esta persona ya es un socio activo");
        }

        
        Socio nuevoSocio = new Socio();
        nuevoSocio.setPersona(persona);
        nuevoSocio.setFechaAlta(LocalDate.now()); 
        nuevoSocio.setEstado("ACTIVO");           
        
        return socioRepository.save(nuevoSocio);
    }
}