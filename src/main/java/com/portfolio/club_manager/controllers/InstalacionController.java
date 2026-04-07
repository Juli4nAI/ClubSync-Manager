package com.portfolio.club_manager.controllers;

import com.portfolio.club_manager.entities.Instalacion;
import com.portfolio.club_manager.services.InstalacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/instalaciones")
public class InstalacionController {

    private final InstalacionService instalacionService;

    public InstalacionController(InstalacionService instalacionService) {
        this.instalacionService = instalacionService;
    }

    
    @GetMapping
    public ResponseEntity<List<Instalacion>> obtenerTodasLasInstalaciones() {

        List<Instalacion> lista = instalacionService.obtenerTodasLasInstalaciones();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Instalacion>> listarDisponibles(
            @RequestParam(required = false) String dia,
            @RequestParam(required = false) LocalTime inicio,
            @RequestParam(required = false) LocalTime fin) {

        
        if (dia != null && inicio != null && fin != null) {
            return ResponseEntity.ok(instalacionService.obtenerDisponiblesEnHorario(dia, inicio, fin));
        }

        
        return ResponseEntity.ok(instalacionService.obtenerDisponiblesGlobal());
    }

    
    @PostMapping
    public ResponseEntity<Instalacion> registrarInstalacion(@RequestBody Instalacion instalacion) {
        Instalacion nuevaInstalacion = instalacionService.registrarInstalacion(instalacion);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaInstalacion);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInstalacion(@PathVariable Integer id) {
        
        instalacionService.eliminarInstalacion(id); 
        return ResponseEntity.noContent().build(); 
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Instalacion> actualizarInstalacion(
            @PathVariable Integer id, 
            @RequestBody Instalacion instalacionActualizada) {
        
        Instalacion actualizada = instalacionService.actualizarInstalacion(id, instalacionActualizada);
        return ResponseEntity.ok(actualizada);
    }

    @GetMapping("/estado")
    public ResponseEntity<List<Instalacion>> obtenerEstadoInstalaciones() {
        List<Instalacion> estado = instalacionService.obtenerEstadoInstalaciones();
        return ResponseEntity.ok(estado);
    }
    

}
