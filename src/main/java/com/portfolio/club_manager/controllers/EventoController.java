package com.portfolio.club_manager.controllers;

import com.portfolio.club_manager.entities.Evento;
import com.portfolio.club_manager.services.EventoService;
import com.portfolio.club_manager.repositories.EventoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final EventoRepository eventoRepository;

    public EventoController(EventoService eventoService, EventoRepository eventoRepository) {
        this.eventoService = eventoService;
        this.eventoRepository = eventoRepository;
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerEventoPorId(@PathVariable Integer id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));
                
        return ResponseEntity.ok(evento);
    }

    @PostMapping
    public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento) {
        Evento nuevoEvento = eventoService.crearEvento(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEvento);
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(eventoService.obtenerTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizarEvento(@PathVariable Integer id, @RequestBody Evento evento) {
        Evento eventoActualizado = eventoService.actualizarEvento(id, evento);
        return ResponseEntity.ok(eventoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Integer id) {
        eventoService.eliminarEvento(id); 
        return ResponseEntity.noContent().build();
    }

    
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarActividad(@PathVariable Integer id) {
        
        eventoService.reactivar(id); 
        return ResponseEntity.ok().build();
    }

    
    @PostMapping("/{idEvento}/inscripcion-persona/{idPersona}")
    public ResponseEntity<?> inscribirPersona(@PathVariable Integer idEvento, @PathVariable Integer idPersona) {
        try {
            
            eventoService.inscribirPersona(idEvento, idPersona);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @DeleteMapping("/{idEvento}/quitar-persona/{idPersona}")
    public ResponseEntity<?> quitarPersona(@PathVariable Integer idEvento, @PathVariable Integer idPersona) {
        try {
            eventoService.quitarPersona(idEvento, idPersona);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}