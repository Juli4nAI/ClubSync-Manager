package com.portfolio.club_manager.repositories;

import com.portfolio.club_manager.entities.Actividad;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Integer> {

    List<Actividad> findByResponsableId(Integer responsableId);

    @Query("SELECT a.nombre, SIZE(a.participantes) FROM Actividad a")
    List<Object[]> obtenerConteoInscriptos();

    @EntityGraph(attributePaths = {"horarios", "instalacion", "responsable"})
    List<Actividad> findAll();

    
    boolean existsByResponsableId(Integer responsableId);

}
