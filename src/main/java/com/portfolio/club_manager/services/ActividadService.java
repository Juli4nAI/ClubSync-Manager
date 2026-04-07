package com.portfolio.club_manager.services;

import com.portfolio.club_manager.entities.Actividad;
import com.portfolio.club_manager.repositories.PersonaRepository;
import com.portfolio.club_manager.entities.Socio;
import com.portfolio.club_manager.entities.Horario;
import com.portfolio.club_manager.entities.Instalacion;
import com.portfolio.club_manager.entities.Persona;
import com.portfolio.club_manager.repositories.ActividadRepository;
import com.portfolio.club_manager.repositories.InstalacionRepository;
import com.portfolio.club_manager.repositories.SocioRepository;
import com.portfolio.club_manager.repositories.HorarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final InstalacionService instalacionService;
    private final SocioRepository socioRepository;
    private final InstalacionRepository instalacionRepository;
    private final HorarioRepository horarioRepository;
    private final PersonaRepository personaRepository;

    public ActividadService(ActividadRepository actividadRepository, InstalacionService instalacionService,
            SocioRepository socioRepository, InstalacionRepository instalacionRepository, HorarioRepository horarioRepository, PersonaRepository personaRepository) {
        this.actividadRepository = actividadRepository;
        this.instalacionService = instalacionService;
        this.socioRepository = socioRepository;
        this.instalacionRepository = instalacionRepository;
        this.horarioRepository = horarioRepository;
        this.personaRepository = personaRepository;
    }

    public Actividad obtenerActividadPorId(Integer id) {
        return actividadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
    }

    public Actividad registrarActividad(Actividad actividad) {

        if (actividad.getNombre() == null || actividad.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la actividad es obligatorio.");
        }

        
        if (actividad.getMonto() == null || actividad.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto es obligatorio y debe ser mayor a cero.");
        }

        Instalacion instalacionReal = instalacionRepository.findById(actividad.getInstalacion().getId())
                .orElseThrow(() -> new IllegalArgumentException("La instalación no existe"));

        actividad.setInstalacion(instalacionReal);

        if (actividad.getInstalacion() == null || actividad.getInstalacion().getId() == null) {
            throw new IllegalArgumentException("La actividad debe estar asignada a una instalación.");
        }

        if (actividad.getResponsable() == null || actividad.getResponsable().getId() == null) {
            throw new IllegalArgumentException("La actividad debe tener un responsable asignado.");
        }

        Socio responsableReal = socioRepository.findById(actividad.getResponsable().getId())
                .orElseThrow(() -> new IllegalArgumentException("El responsable no existe"));
                
        actividad.setResponsable(responsableReal.getPersona());

        if (actividad.getParticipantes() == null) {
            actividad.setParticipantes(new java.util.ArrayList<>());
        }

        
        if (actividad.getParticipantes().size() > actividad.getInstalacion().getCapacidad()) {
            throw new IllegalArgumentException("La capacidad de la actividad excede la capacidad de la instalación.");
        }

        if (actividad.getHorarios() == null) {
            actividad.setHorarios(new java.util.ArrayList<>());
        }

        
        for (Horario horario : actividad.getHorarios()) {
            boolean libre = instalacionService.estaDisponible(
                    actividad.getInstalacion().getId(),
                    horario.getDiaSemana(),
                    horario.getHoraInicio(),
                    horario.getHoraFin());

            if (!libre) {
                throw new IllegalArgumentException("La instalación " + actividad.getInstalacion().getNombre() +
                        " está ocupada el " + horario.getDiaSemana() +
                        " de " + horario.getHoraInicio() + " a " + horario.getHoraFin());
            }
        }

        actividad.setEstado("ACTIVO");
        return actividadRepository.save(actividad);
    }

    @Transactional
    public void inscribirSocio(Integer idActividad, Integer idSocio) {
        
        Actividad actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
                
        Socio socio = socioRepository.findById(idSocio)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado"));

        
        if (actividad.getParticipantes().contains(socio)) {
            throw new IllegalArgumentException("El socio ya está inscrito en esta actividad");
        }

        
        actividad.getParticipantes().add(socio);
        actividadRepository.save(actividad);
    }

    public List<Actividad> obtenerTodasLasActividades() {
        return actividadRepository.findAll();
    }

    public List<Socio> obtenerParticipantesDeActividad(Integer actividadId) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));

        return actividad.getParticipantes();
    }

    public List<Actividad> obtenerActividadesPorResponsable(Integer responsableId) {
        return actividadRepository.findByResponsableId(responsableId);
    }


    
    

    @Transactional 
    public Actividad actualizarActividad(Integer id, Actividad datosActualizados) {
        Actividad existente = actividadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));

        
        existente.setNombre(datosActualizados.getNombre());
        existente.setMonto(datosActualizados.getMonto());

        
        if (datosActualizados.getInstalacion() != null && datosActualizados.getInstalacion().getId() != null) {
            Instalacion instalacionReal = instalacionRepository.findById(datosActualizados.getInstalacion().getId())
                    .orElseThrow(() -> new IllegalArgumentException("La instalación no existe"));
            existente.setInstalacion(instalacionReal);
        }

        
        if (datosActualizados.getResponsable() != null && datosActualizados.getResponsable().getId() != null) {
            Persona responsableReal = personaRepository.findById(datosActualizados.getResponsable().getId())
                    .orElseThrow(() -> new IllegalArgumentException("El responsable no existe"));
            existente.setResponsable(responsableReal); 
        }

        
        return actividadRepository.saveAndFlush(existente);
    }

    public void eliminarActividad(Integer id) {
        Actividad actividad = actividadRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
            
        actividad.setEstado("INACTIVO");
        
        actividadRepository.save(actividad);
    }

    public Horario agregarHorarioConValidacion(Integer actividadId, Horario nuevoHorario) {
        Actividad actividad = actividadRepository.findById(actividadId)
            .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
            
        Instalacion instalacion = actividad.getInstalacion();

        
        
        List<Horario> horariosDelDia = horarioRepository.findByActividad_Instalacion_IdAndDiaSemana(
                instalacion.getId(), nuevoHorario.getDiaSemana());

        
        for (Horario hExistente : horariosDelDia) {
            
            boolean choca = nuevoHorario.getHoraInicio().isBefore(hExistente.getHoraFin()) &&
                            nuevoHorario.getHoraFin().isAfter(hExistente.getHoraInicio());

            if (choca) {
                
                throw new IllegalArgumentException(
                    "Error de colisión: La instalación ya está ocupada por '" + 
                    hExistente.getActividad().getNombre() + 
                    "' de " + hExistente.getHoraInicio() + " a " + hExistente.getHoraFin()
                );
            }
        }

        
        nuevoHorario.setActividad(actividad);
        return horarioRepository.save(nuevoHorario);
    }

    public void eliminarHorario(Integer horarioId) {
    
    Horario horario = horarioRepository.findById(horarioId)
        .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

         horarioRepository.delete(horario);
    }

    public Actividad reactivar(Integer id) {
        Actividad actividad = actividadRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));

        actividad.setEstado("ACTIVO");
        return actividadRepository.save(actividad);
    }

    @Transactional
    public void quitarSocio(Integer idActividad, Integer idSocio) {
        Actividad actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
                
        Socio socio = socioRepository.findById(idSocio)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado"));

        
        actividad.getParticipantes().remove(socio);
        actividadRepository.save(actividad);
    }

}
