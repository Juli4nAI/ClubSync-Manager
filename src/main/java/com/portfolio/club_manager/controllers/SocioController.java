package com.portfolio.club_manager.controllers;

import com.portfolio.club_manager.entities.Socio;
import com.portfolio.club_manager.services.SocioService;
import com.portfolio.club_manager.repositories.SocioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/socios")
public class SocioController {

    private final SocioService socioService;
    private final SocioRepository socioRepository;

    public SocioController(SocioService socioService, SocioRepository socioRepository) {
        this.socioService = socioService;
        this.socioRepository = socioRepository;
    }

    @PostMapping
    public ResponseEntity<Socio> crearSocio(@RequestBody Socio socio) {
        Socio nuevoSocio = socioService.registrarSocio(socio);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoSocio);
    }

    @GetMapping
    public ResponseEntity<List<Socio>> listarSocios() {
        return ResponseEntity.ok(socioService.obtenerTodosLosSocios());
    }

    @PostMapping("/promocionar/{personaId}")
    public ResponseEntity<?> promocionar(@PathVariable Integer personaId) {
        try {
            Socio socioCreado = socioService.promocionarASocio(personaId);
            return ResponseEntity.ok(socioCreado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<?> darDeBajaSocio(@PathVariable Integer id) {
        
        return socioRepository.findById(id).map(socio -> {
            
            socio.setEstado("BAJA");
            
            socioRepository.save(socio);
            return ResponseEntity.ok("Socio dado de baja correctamente.");
        }).orElseGet(() -> ResponseEntity.badRequest().body("No se encontró el socio."));
    }

    @PutMapping("/{id}/reasociar")
    public ResponseEntity<?> reasociarSocio(@PathVariable Integer id) {
        return socioRepository.findById(id).map(socio -> {
            
            
            if (socio.getPersona() != null && socio.getPersona().getDeuda() != null && socio.getPersona().getDeuda() > 0) {
                return ResponseEntity.badRequest().body("Operación denegada: El socio mantiene una deuda activa de $" + socio.getPersona().getDeuda() + ". Debe regularizarla primero.");
            }

            
            socio.setEstado("ACTIVO");
            socio.setFechaAlta(LocalDate.now()); 
            socioRepository.save(socio);
            
            return ResponseEntity.ok("Socio reasociado correctamente.");
        }).orElseGet(() -> ResponseEntity.badRequest().body("No se encontró el historial del socio."));
    }
}