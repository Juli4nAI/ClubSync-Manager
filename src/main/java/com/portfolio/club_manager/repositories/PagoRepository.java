package com.portfolio.club_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.portfolio.club_manager.entities.Pago;

import java.util.List;

import java.time.LocalDateTime; 

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    
    List<Pago> findByPersonaIdOrderByFechaHoraDesc(Integer personaId);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.fechaHora BETWEEN :inicio AND :fin")
    Double sumarPagosEntreFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT p.concepto, SUM(p.monto) FROM Pago p GROUP BY p.concepto")
    List<Object[]> sumarRecaudacionPorTipo();
}
