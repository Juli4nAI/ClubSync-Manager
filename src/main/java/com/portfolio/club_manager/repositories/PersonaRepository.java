package com.portfolio.club_manager.repositories;

import com.portfolio.club_manager.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    public List<Persona> findByDeudaGreaterThan(double deuda);
}
