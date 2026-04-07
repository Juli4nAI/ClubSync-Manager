package com.portfolio.club_manager.services;

import com.portfolio.club_manager.entities.Horario;
import com.portfolio.club_manager.repositories.HorarioRepository;
import org.springframework.stereotype.Service;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public Horario guardar(Horario horario) {
        return horarioRepository.save(horario);
    }
}