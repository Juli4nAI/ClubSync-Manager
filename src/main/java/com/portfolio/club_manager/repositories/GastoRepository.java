package com.portfolio.club_manager.repositories;

import com.portfolio.club_manager.entities.Gasto;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Integer> {

    @Query("SELECT SUM(g.monto) FROM Gasto g")
    Double sumMonto();

    @Query("SELECT SUM(g.monto) FROM Gasto g WHERE g.fecha BETWEEN :inicio AND :fin")
    Double sumarGastosEntreFechas(LocalDate inicio, LocalDate fin);
}
