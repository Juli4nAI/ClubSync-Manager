package com.portfolio.club_manager.repositories;

import com.portfolio.club_manager.entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Integer> {

    
    
    
    
    @Query("SELECT h FROM Horario h WHERE h.actividad.instalacion.id = :instalacionId " +
            "AND h.diaSemana = :diaSemana " +
            "AND h.horaInicio < :horaFin " +
            "AND h.horaFin > :horaInicio")
    List<Horario> buscarSuperposiciones(
            @Param("instalacionId") Integer instalacionId,
            @Param("diaSemana") String diaSemana,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);

    List<Horario> findByActividad_Instalacion_IdAndDiaSemana(Integer instalacionId, String diaSemana);
}

