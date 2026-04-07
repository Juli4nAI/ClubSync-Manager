package com.portfolio.club_manager.repositories;

import com.portfolio.club_manager.entities.Instalacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface InstalacionRepository extends JpaRepository<Instalacion, Integer> {

    @Query("SELECT i FROM Instalacion i WHERE i NOT IN (SELECT a.instalacion FROM ActividadBase a)")
    List<Instalacion> findInstalacionesDisponibles();


    @Query("SELECT i FROM Instalacion i WHERE i IN (SELECT a.instalacion FROM ActividadBase a)")
    List<Instalacion> findInstalacionesOcupadas();

    @Query("SELECT i FROM Instalacion i WHERE i NOT IN (" +
           "SELECT a.instalacion FROM ActividadBase a JOIN a.horarios h " +
           "WHERE h.diaSemana = :dia " +
           "AND h.horaInicio < :fin AND h.horaFin > :ini)")
    List<Instalacion> findLibresEnHorario(
        @Param("dia") String dia, 
        @Param("ini") LocalTime ini, 
        @Param("fin") LocalTime fin);

    
    @Query("SELECT i.nombre, COUNT(a) FROM Instalacion i LEFT JOIN Actividad a ON a.instalacion = i GROUP BY i.nombre")
    List<Object[]> contarActividadesPorInstalacion();

    public void deleteById(Integer id);

    @Query("SELECT i FROM Instalacion i LEFT JOIN Actividad a ON a.instalacion = i GROUP BY i.id")
    List<Instalacion> findEstadoInstalaciones();

}