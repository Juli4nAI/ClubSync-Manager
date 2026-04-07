package com.portfolio.club_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;

import com.portfolio.club_manager.entities.Pago;
import com.portfolio.club_manager.entities.Persona;
import com.portfolio.club_manager.services.PagoService;
import com.portfolio.club_manager.services.PersonaService;

import java.util.Map;
import java.time.LocalDateTime;

@RestController
public class WebhookController {

    
    @Autowired
    private PersonaService personaService;

    @Autowired
    private PagoService pagoService;

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirNotificacionMP(@RequestBody Map<String, Object> payload) {
        try {
            if ("payment".equals(payload.get("type"))) {
                
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                String idPagoStr = data.get("id").toString();
                Long idPago = Long.valueOf(idPagoStr);

                PaymentClient client = new PaymentClient();
                Payment pago = client.get(idPago);

                if ("approved".equals(pago.getStatus())) {
                    String idSocioStr = pago.getExternalReference();
                    Integer idSocio = Integer.valueOf(idSocioStr);

                    System.out.println("✅ Pago real y aprobado para el socio ID: " + idSocio);
                    
                    
                    
                    Persona socio = personaService.buscarPorId(idSocio); 

                    
    
                    Pago nuevoPago = new Pago();
                    nuevoPago.setFechaHora(LocalDateTime.now());
                    nuevoPago.setMonto(pago.getTransactionAmount().doubleValue());
                    nuevoPago.setPersona(socio);
                    nuevoPago.setMetodo("Mercado Pago");
                    
                    pagoService.guardar(nuevoPago);
                    
                    if (socio != null) {
                        
                        Double montoPagado = pago.getTransactionAmount().doubleValue();
                        socio.setDeuda(socio.getDeuda() - montoPagado);

                        
                        personaService.guardar(socio); 
                        
                        System.out.println("💾 ¡Éxito! Base de datos actualizada. Deuda en 0 para: " + socio.getApynom());
                    }
                }
            }
            return ResponseEntity.ok("Recibido");

        } catch (Exception e) {
            System.err.println("Error procesando el Webhook: " + e.getMessage());
            return ResponseEntity.ok("Recibido con errores internos");
        }
    }
}