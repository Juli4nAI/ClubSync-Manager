package com.portfolio.club_manager.repositories;

import com.portfolio.club_manager.entities.Evento;
import com.portfolio.club_manager.entities.Persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {

    @Query("SELECT e.participantes FROM Evento e WHERE e.id = :eventoId")
    List<Persona> listarParticipantesEvento(@Param("eventoId") Long eventoId);

    boolean existsByResponsableId(Integer responsableId);

    List<Evento> findByResponsableId(Integer responsableId);

}
