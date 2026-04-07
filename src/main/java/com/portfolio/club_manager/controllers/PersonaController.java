package com.portfolio.club_manager.controllers;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.portfolio.club_manager.entities.Actividad;
import com.portfolio.club_manager.entities.Persona;
import com.portfolio.club_manager.entities.Evento;
import com.portfolio.club_manager.entities.Pago;
import com.portfolio.club_manager.services.PersonaService;

import jakarta.transaction.Transactional;

import com.portfolio.club_manager.repositories.PersonaRepository;
import com.portfolio.club_manager.repositories.ActividadRepository;
import com.portfolio.club_manager.repositories.EventoRepository;
import com.portfolio.club_manager.repositories.PagoRepository;
import com.portfolio.club_manager.repositories.SocioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.math.BigDecimal;
import java.util.List;

import com.mercadopago.exceptions.MPApiException;


@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;
    private final PersonaRepository personaRepository;
    private final ActividadRepository actividadRepository;
    private final EventoRepository eventoRepository;
    private final PagoRepository pagoRepository;
    private final SocioRepository socioRepository;

    @Value("${mercadopago.access-token}")
    private String mercadoPagoAccessToken;

    public PersonaController(PersonaService personaService, PersonaRepository personaRepository, ActividadRepository actividadRepository, EventoRepository eventoRepository, PagoRepository pagoRepository, SocioRepository socioRepository) {
        this.personaService = personaService;
        this.personaRepository = personaRepository;
        this.actividadRepository = actividadRepository;
        this.eventoRepository = eventoRepository;
        this.pagoRepository = pagoRepository;
        this.socioRepository = socioRepository;
    }

    
    @GetMapping
    public List<Persona> listarPersonas() {
        return personaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPersonaPorId(@PathVariable Integer id) {
        
        
        Persona persona = personaService.buscarPorId(id);
        
        if (persona != null) {
            
            return ResponseEntity.ok(persona);
        } else {
            
            return ResponseEntity.notFound().build();
        }
    }

    
    @PostMapping
    public ResponseEntity<Persona> registrarPersona(@RequestBody Persona persona) {
        
        

        Persona nuevaPersona = personaService.registrarPersona(persona);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersona);
    }

   @PutMapping("/{id}/baja")
    @Transactional
    public ResponseEntity<?> darDeBajaPersona(@PathVariable Integer id) {
        return personaRepository.findById(id).map(persona -> {
            
            
            persona.setEstado("INACTIVO");

            
            
            socioRepository.findByPersona(persona).ifPresent(socio -> {
                socio.setEstado("BAJA");
                socioRepository.save(socio);
            });
            
            /* NOTA: Si en tu base de datos el Socio tiene un ID distinto al de la Persona 
               (relación 1 a 1 clásica), la línea de arriba sería:
               socioRepository.findByPersonaId(id).ifPresent( ... )
            */

            
            List<Actividad> susActividades = actividadRepository.findByResponsableId(id);
            for(Actividad act : susActividades) {
                act.setResponsable(null); 
            }

            List<Evento> susEventos = eventoRepository.findByResponsableId(id);
            for(Evento ev : susEventos) {
                ev.setResponsable(null); 
            }

            actividadRepository.saveAll(susActividades);
            eventoRepository.saveAll(susEventos);

            
            personaRepository.save(persona);
            
            return ResponseEntity.ok("Persona dada de baja correctamente.");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la persona."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Persona> actualizarPersona(@PathVariable Integer id, @RequestBody Persona personaDetalles) {
        
        
        Persona personaExistente = personaService.buscarPorId(id);
        
        if (personaExistente == null) {
            return ResponseEntity.notFound().build(); 
        }

        
        personaExistente.setApynom(personaDetalles.getApynom());
        personaExistente.setDni(personaDetalles.getDni());
        personaExistente.setCelular(personaDetalles.getCelular());
        personaExistente.setEmail(personaDetalles.getEmail());

        
        personaService.guardar(personaExistente);
        
        return ResponseEntity.ok(personaExistente); 
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarPersona(@PathVariable Integer id) {
        return personaRepository.findById(id).map(persona -> {
            persona.setEstado("ACTIVO");
            personaRepository.save(persona);
            return ResponseEntity.ok("Persona reactivada correctamente.");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la persona."));
    }

    @GetMapping("/{id}/es-responsable")
    public ResponseEntity<Boolean> verificarSiEsResponsable(@PathVariable Integer id) {
        
        
        boolean esResponsableDeActividad = actividadRepository.existsByResponsableId(id);
        boolean esResponsableDeEvento = eventoRepository.existsByResponsableId(id);
        
        boolean esResponsable = esResponsableDeActividad || esResponsableDeEvento;
        
        return ResponseEntity.ok(esResponsable);
    }


    @PostMapping("/{id}/pagar") 
    public ResponseEntity<?> registrarPago(@PathVariable Integer id, @RequestParam Double monto, @RequestParam String concepto) {
        return personaRepository.findById(id).map(persona -> {
            
            Double deudaActual = persona.getDeuda() != null ? persona.getDeuda() : 0.0;
            
            if (monto <= 0) return ResponseEntity.badRequest().body("El monto a pagar debe ser mayor a cero.");
            
            
            persona.setDeuda(deudaActual - monto);
            personaRepository.save(persona);

            
            Pago nuevoPago = new Pago();
            nuevoPago.setMonto(monto);
            nuevoPago.setConcepto(concepto);
            nuevoPago.setPersona(persona);
            pagoRepository.save(nuevoPago);
            
            return ResponseEntity.ok("Pago registrado con éxito.");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada."));
    }

    
    @GetMapping("/{id}/pagos")
    public ResponseEntity<?> obtenerHistorialPagos(@PathVariable Integer id) {
        List<Pago> historial = pagoRepository.findByPersonaIdOrderByFechaHoraDesc(id);
        return ResponseEntity.ok(historial);
    }


    @PostMapping("/{id}/generar-qr")
    public ResponseEntity<?> generarQrDePago(@PathVariable Integer id, 
                                            @RequestParam Double monto, 
                                            @RequestParam String concepto) {
        try {
            MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(concepto) 
                    .description(concepto)
                    .quantity(1)
                    .unitPrice(new BigDecimal(monto)) 
                    .currencyId("ARS")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(Collections.singletonList(item))
                    .externalReference(id.toString()) 
                    .statementDescriptor("CLUB MANAGER")
                    
                    .notificationUrl("https://sook-superindignant-undefeatedly.ngrok-free.dev/webhook")
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            

            return ResponseEntity.ok(Map.of("urlPago", preference.getInitPoint()));

        } catch (MPApiException apiEx) {
            System.err.println("=== ERROR MP ===");
            System.err.println("Status: " + apiEx.getStatusCode());
            System.err.println("Body: " + apiEx.getApiResponse().getContent());
            return ResponseEntity.internalServerError().body(apiEx.getApiResponse().getContent());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
}
