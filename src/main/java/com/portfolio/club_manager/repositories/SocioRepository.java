package com.portfolio.club_manager.repositories;

import com.portfolio.club_manager.dtos.MorosoDTO;
import com.portfolio.club_manager.entities.Persona;
import com.portfolio.club_manager.entities.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Integer> {

    List<Socio> findByEstado(String estado);

    Boolean existsByPersonaId(Integer persona_id);

    Optional<Socio> findByPersona(Persona persona);

   @Query("SELECT new com.portfolio.club_manager.dtos.MorosoDTO(s.id, p.apynom, p.dni, p.deuda) " +
           "FROM Socio s " +
           "JOIN s.persona p " +
           "WHERE s.estado = 'activo' AND p.deuda > 0")
    List<MorosoDTO> buscarMorosos();

    @Query("SELECT s.estado, COUNT(s) FROM Socio s GROUP BY s.estado")
    List<Object[]> contarSociosPorEstado();

}
