package com.portfolio.club_manager.services;

import com.portfolio.club_manager.entities.Gasto;
import com.portfolio.club_manager.repositories.GastoRepository;
import com.portfolio.club_manager.repositories.PagoRepository; 
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class GastoService {

    private final GastoRepository gastoRepository;
    private final PagoRepository pagoRepository; 

    public GastoService(GastoRepository gastoRepository, PagoRepository pagoRepository) {
        this.gastoRepository = gastoRepository;
        this.pagoRepository = pagoRepository;
    }

    public Gasto registrarGasto(Gasto gasto) {
        if (gasto.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto del gasto debe ser mayor a cero.");
        }

        if (gasto.getFecha() == null) {
            throw new IllegalArgumentException("La fecha del gasto es obligatoria.");
        }

        if (gasto.getDescripcion() == null || gasto.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripción del gasto es obligatoria.");
        }
        return gastoRepository.save(gasto);
    }

    public List<Gasto> obtenerTodosLosGastos() {
        return gastoRepository.findAll();
    }

    public Double calcularGastosTotales() {
        Double gastos = gastoRepository.sumMonto();
        if (gastos == null) {
            return 0.0;
        }

        return gastos;
    }

    
    public Double calcularBalanceEntreFechas(LocalDate inicio, LocalDate fin) {
        
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(LocalTime.MAX);

        Double ingresos = pagoRepository.sumarPagosEntreFechas(inicioDateTime, finDateTime);
        if (ingresos == null) ingresos = 0.0;

        Double gastos = this.calcularGastosEntreFechas(inicio, fin);
        if (gastos == null) gastos = 0.0;

        return ingresos - gastos;
    }

    public Double calcularGastosEntreFechas(LocalDate inicio, LocalDate fin) {
        Double total = gastoRepository.sumarGastosEntreFechas(inicio, fin);
        return (total != null) ? total : 0.0;
    }
}