package com.portfolio.club_manager.services;

import com.portfolio.club_manager.entities.Evento;
import com.portfolio.club_manager.entities.Instalacion;
import com.portfolio.club_manager.entities.Persona;
import com.portfolio.club_manager.repositories.EventoRepository;
import com.portfolio.club_manager.repositories.InstalacionRepository;
import com.portfolio.club_manager.repositories.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final InstalacionRepository instalacionRepository;
    private final PersonaRepository personaRepository;

    public EventoService(EventoRepository eventoRepository, InstalacionRepository instalacionRepository, PersonaRepository personaRepository) {
        this.eventoRepository = eventoRepository;
        this.instalacionRepository = instalacionRepository;
        this.personaRepository = personaRepository;
    }

    public Evento crearEvento(Evento evento) {
        if (evento.getNombre() == null || evento.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del evento es obligatorio.");
        }
        if (evento.getParticipantes() == null) {
            evento.setParticipantes(new java.util.ArrayList<>());
        }
        return eventoRepository.save(evento);
    }

    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public Evento actualizarEvento(Integer id, Evento datosActualizados) {
        Evento eventoExistente = eventoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        if (datosActualizados.getNombre() != null && !datosActualizados.getNombre().trim().isEmpty()) {
            eventoExistente.setNombre(datosActualizados.getNombre());
        }

         eventoExistente.setMonto(datosActualizados.getMonto());

        
        if (datosActualizados.getInstalacion() != null && datosActualizados.getInstalacion().getId() != null) {
            Instalacion instalacionReal = instalacionRepository.findById(datosActualizados.getInstalacion().getId())
                    .orElseThrow(() -> new IllegalArgumentException("La instalación no existe"));
            eventoExistente.setInstalacion(instalacionReal);
        }

        
        if (datosActualizados.getResponsable() != null && datosActualizados.getResponsable().getId() != null) {
            Persona responsableReal = personaRepository.findById(datosActualizados.getResponsable().getId())
                    .orElseThrow(() -> new IllegalArgumentException("El responsable no existe"));
            eventoExistente.setResponsable(responsableReal); 
        }


        return eventoRepository.save(eventoExistente);
    }

    public void eliminarEvento(Integer id) {
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        evento.setEstado("INACTIVO");

        eventoRepository.save(evento);
    }

    public void reactivar(Integer id) {
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        evento.setEstado("ACTIVO");

        eventoRepository.save(evento);
    }

    public void inscribirPersona(Integer idEvento, Integer idPersona) {
        Evento evento = eventoRepository.findById(idEvento)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        Persona persona = personaRepository.findById(idPersona)
            .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));

        if (evento.getParticipantes().contains(persona)) {
            throw new IllegalArgumentException("La persona ya está inscripta en el evento");
        }

        evento.getParticipantes().add(persona);
        eventoRepository.save(evento);
    }

    public void quitarPersona(Integer idEvento, Integer idPersona) {
        Evento evento = eventoRepository.findById(idEvento)
            .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        Persona persona = personaRepository.findById(idPersona)
            .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));

        if (!evento.getParticipantes().contains(persona)) {
            throw new IllegalArgumentException("La persona no está inscripta en el evento");
        }

        evento.getParticipantes().remove(persona);
        eventoRepository.save(evento);
    }
}