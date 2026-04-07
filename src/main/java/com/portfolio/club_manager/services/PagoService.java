package com.portfolio.club_manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.portfolio.club_manager.entities.Pago;
import com.portfolio.club_manager.repositories.PagoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    public void guardar(Pago pago) {
        pagoRepository.save(pago);
    }

    public Pago buscarPorId(Integer id) {
        return pagoRepository.findById(id).orElse(null);
    }
}