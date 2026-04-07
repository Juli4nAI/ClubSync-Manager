package com.portfolio.club_manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.portfolio.club_manager.entities.*;
import com.portfolio.club_manager.repositories.*;


@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private InstalacionRepository instalacionRepository;
    
    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private EventoRepository eventoRepository;
    
    

    @Override
    public void run(String... args) throws Exception {
        
        
        if (instalacionRepository.count() == 0) {
            System.out.println("🌱 Iniciando la siembra de datos de prueba...");

            
            Instalacion inst1 = new Instalacion();
            inst1.setNombre("Cancha de Tenis Principal");
            inst1.setCapacidad(4);
            inst1.setEstado("DISPONIBLE");
            inst1.setDescripcion("Cancha de polvo de ladrillo iluminada");
            instalacionRepository.save(inst1);

            Instalacion inst2 = new Instalacion();
            inst2.setNombre("Pileta Olímpica");
            inst2.setCapacidad(50);
            inst2.setEstado("DISPONIBLE");
            inst2.setDescripcion("Pileta climatizada de 50 metros");
            instalacionRepository.save(inst2);

            
            Persona p1 = new Persona();
            p1.setApynom("Lionel Messi");
            p1.setDni("30123456");
            p1.setEmail("lio@intermiami.com");
            p1.setDeuda(0.0);
            p1.setEstado("ACTIVO");
            personaRepository.save(p1);

            Persona p2 = new Persona();
            p2.setApynom("Facundo Campazzo");
            p2.setDni("35987654");
            p2.setEmail("facu@realmadrid.com");
            p2.setDeuda(15000.0); 
            p2.setEstado("ACTIVO");
            personaRepository.save(p2);


            
            Socio socio1 = new Socio();
            socio1.setPersona(p1); 
            socio1.setEstado("ACTIVO");
            socioRepository.save(socio1);

            Socio socio2 = new Socio();
            socio2.setPersona(p2); 
            socio2.setEstado("ACTIVO");
            socioRepository.save(socio2);

            
            Actividad act1 = new Actividad();
            act1.setNombre("Tenis Competitivo");
            act1.setMonto(8500.0);
            act1.setEstado("ACTIVO");
            act1.setInstalacion(inst1); 
            act1.setResponsable(p1);    
            actividadRepository.save(act1);

            Actividad act2 = new Actividad();
            act2.setNombre("Taller de Programación - Pibes del Código");
            act2.setMonto(0.0); 
            act2.setEstado("ACTIVO");
            act2.setInstalacion(inst2); 
            act2.setResponsable(p2);    
            actividadRepository.save(act2);

            Evento ev1 = new Evento();
            ev1.setNombre("Torneo de Verano Pibes del Destino");
            ev1.setMonto(12000.0);
            ev1.setEstado("ACTIVO");
            ev1.setInstalacion(inst1);
            ev1.setResponsable(p1);
            eventoRepository.save(ev1);

            System.out.println("✅ ¡Datos de prueba cargados con éxito!");
        }
    }
}
