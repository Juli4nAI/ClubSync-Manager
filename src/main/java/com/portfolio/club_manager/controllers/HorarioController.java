package com.portfolio.club_manager.controllers;

import com.portfolio.club_manager.entities.Horario;
import com.portfolio.club_manager.services.HorarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @PostMapping
    public ResponseEntity<Horario> crearHorario(@RequestBody Horario horario) {
        Horario nuevoHorario = horarioService.guardar(horario);
        return ResponseEntity.ok(nuevoHorario);
    }
}