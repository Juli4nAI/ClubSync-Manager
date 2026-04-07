package com.portfolio.club_manager.controllers;

import com.portfolio.club_manager.entities.Actividad;
import com.portfolio.club_manager.entities.Horario;
import com.portfolio.club_manager.entities.Socio;
import com.portfolio.club_manager.services.ActividadService;
import com.portfolio.club_manager.repositories.ActividadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadService actividadService;
    private final ActividadRepository actividadRepository;

    public ActividadController(ActividadService actividadService, ActividadRepository actividadRepository) {
        this.actividadService = actividadService;
        this.actividadRepository = actividadRepository;
    }

    
    
    @GetMapping("/{id}")
    public ResponseEntity<Actividad> obtenerActividad(@PathVariable Integer id) {
        Actividad actividad = actividadRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
            
        return ResponseEntity.ok(actividad);
    }

    @GetMapping
    public ResponseEntity<List<Actividad>> obtenerTodasLasActividades() {

        List<Actividad> lista = actividadService.obtenerTodasLasActividades();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{actividadId}/participantes")
    public ResponseEntity<List<Socio>> obtenerParticipantesDeActividad(@PathVariable Integer actividadId) {

        List<Socio> participantes = actividadService.obtenerParticipantesDeActividad(actividadId);

        return ResponseEntity.ok(participantes);
    }

    @GetMapping("/responsable/{responsableId}")
    public ResponseEntity<List<Actividad>> obtenerActividadesPorResponsable(@PathVariable Integer responsableId) {
        List<Actividad> actividades = actividadService.obtenerActividadesPorResponsable(responsableId);
        return ResponseEntity.ok(actividades);
    }

    
    @GetMapping("/{actividadId}/horarios")
    public ResponseEntity<List<Horario>> obtenerHorariosDeActividad(@PathVariable Integer actividadId) {
        
        
        
        Actividad actividad = actividadService.obtenerActividadPorId(actividadId); 
        
        
        return ResponseEntity.ok(actividad.getHorarios());
    }
    


    
    @PostMapping
    public ResponseEntity<Actividad> registrarActividad(@RequestBody Actividad actividad) {
        Actividad nuevaActividad = actividadService.registrarActividad(actividad);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaActividad);
    }

    @PostMapping("/{idActividad}/inscripcion-socio/{idSocio}")
    public ResponseEntity<?> inscribirSocio(@PathVariable Integer idActividad, @PathVariable Integer idSocio) {
        try {
            
            actividadService.inscribirSocio(idActividad, idSocio);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Actividad> actualizarActividad(
            @PathVariable Integer id, 
            @RequestBody Actividad actividad) {
            
        Actividad actualizada = actividadService.actualizarActividad(id, actividad);
        return ResponseEntity.ok(actualizada);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarActividad(@PathVariable Integer id) {
        actividadService.eliminarActividad(id);
        return ResponseEntity.noContent().build();
    }

    
    @PostMapping("/{actividadId}/horarios")
    public ResponseEntity<?> agregarHorarioAActividad(
            @PathVariable Integer actividadId, 
            @RequestBody Horario nuevoHorario) {
        try {
            
            Horario guardado = actividadService.agregarHorarioConValidacion(actividadId, nuevoHorario);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException e) {
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{actividadId}/horarios/{horarioId}")
    public ResponseEntity<Void> quitarHorario(
            @PathVariable Integer actividadId, 
            @PathVariable Integer horarioId) {

        actividadService.eliminarHorario(horarioId);
        return ResponseEntity.noContent().build();
    }

    
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarActividad(@PathVariable Integer id) {
        
        actividadService.reactivar(id); 
        return ResponseEntity.ok().build();
    }

    
    @DeleteMapping("/{idActividad}/quitar-socio/{idSocio}")
    public ResponseEntity<?> quitarSocio(@PathVariable Integer idActividad, @PathVariable Integer idSocio) {
        try {
            actividadService.quitarSocio(idActividad, idSocio);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
