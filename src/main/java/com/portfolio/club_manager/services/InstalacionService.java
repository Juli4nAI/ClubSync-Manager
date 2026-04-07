package com.portfolio.club_manager.services;

import com.portfolio.club_manager.entities.Instalacion;
import com.portfolio.club_manager.entities.Horario;
import com.portfolio.club_manager.repositories.InstalacionRepository;
import com.portfolio.club_manager.repositories.HorarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalTime;

@Service
public class InstalacionService {

    
    private final InstalacionRepository instalacionRepository;
    private final HorarioRepository horarioRepository;

    
    public InstalacionService(InstalacionRepository instalacionRepository, HorarioRepository horarioRepository) {
        this.instalacionRepository = instalacionRepository;
        this.horarioRepository = horarioRepository;
    }

    public Instalacion registrarInstalacion(Instalacion instalacion) {

        if (instalacion.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a cero");
        }

        return instalacionRepository.save(instalacion);
    }

    public List<Instalacion> obtenerTodasLasInstalaciones() {
        return instalacionRepository.findAll();
    }

    public boolean estaDisponible(Integer instalacionId, String diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        
        List<Horario> choques = horarioRepository.buscarSuperposiciones(instalacionId, diaSemana, horaInicio, horaFin);

        
        return choques.isEmpty();
    }

    public List<Instalacion> obtenerDisponiblesGlobal() {
        return instalacionRepository.findInstalacionesDisponibles();
    }

    
    public List<Instalacion> obtenerDisponiblesEnHorario(String dia, LocalTime inicio, LocalTime fin) {
        return instalacionRepository.findLibresEnHorario(dia, inicio, fin);
    }

    public List<Instalacion> obtenerOcupadas() {
        return instalacionRepository.findInstalacionesOcupadas();
    }

    public void eliminarInstalacion(Integer id) {
        
        Instalacion existente = instalacionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Instalación no encontrada"));
            
        
        existente.setEstado("INACTIVO");
        
        
        instalacionRepository.save(existente);
    }

    public Instalacion actualizarInstalacion(Integer id, Instalacion datosNuevos) {
        
        Instalacion existente = instalacionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Instalación no encontrada"));
            
        
        existente.setNombre(datosNuevos.getNombre());
        existente.setCapacidad(datosNuevos.getCapacidad());
        existente.setEstado(datosNuevos.getEstado());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setAncho(datosNuevos.getAncho());
        existente.setLargo(datosNuevos.getLargo());
        
        
        return instalacionRepository.save(existente);
    }

        public List<Instalacion> obtenerEstadoInstalaciones() {
            return instalacionRepository.findEstadoInstalaciones();
        }

        
}