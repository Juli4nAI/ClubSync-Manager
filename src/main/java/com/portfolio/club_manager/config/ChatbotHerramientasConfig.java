package com.portfolio.club_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

import com.portfolio.club_manager.entities.Persona;
import com.portfolio.club_manager.entities.Actividad;
import com.portfolio.club_manager.entities.ActividadBase;
import com.portfolio.club_manager.entities.Evento;
import com.portfolio.club_manager.entities.Instalacion;
import com.portfolio.club_manager.repositories.PersonaRepository;
import com.portfolio.club_manager.repositories.SocioRepository;
import com.portfolio.club_manager.repositories.ActividadRepository;
import com.portfolio.club_manager.repositories.EventoRepository;
import com.portfolio.club_manager.repositories.InstalacionRepository;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@Configuration
public class ChatbotHerramientasConfig {

    @Autowired
    private PersonaRepository personaRepository;
    
    @Autowired
    private InstalacionRepository instalacionRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private ActividadRepository actividadRepository;
    
    @Autowired
    private EventoRepository eventoRepository;

    /* =========================================================
     * 1. CLASES DE APOYO (Records para que la IA sepa qué pedir)
     * ========================================================= */
    public record PersonaRequest(String nombre) {}
    public record ConsultaVaciaRequest() {} 

    /* =========================================================
     * 2. HERRAMIENTA: Consultar Personas / Socios
     * ========================================================= */
    @Bean
    @Description("Busca información detallada de un socio o persona específica por su nombre o apellido (para saber su deuda, rol, estado, email o DNI).")
    public Function<PersonaRequest, String> consultarPersonaTool() {
        return request -> {
            List<Persona> todas = (List<Persona>) personaRepository.findAll();
            
            String info = todas.stream()
                .filter(p -> p.getApynom().toLowerCase().contains(request.nombre().toLowerCase()))
                .map(p -> String.format("Nombre: %s | Estado: %s | Deuda: $%s | Contacto: %s", 
                          p.getApynom(), p.getEstado(), p.getDeuda(), p.getEmail()))
                .collect(Collectors.joining("\n"));
                
            return info.isEmpty() ? "No se encontró a nadie con el nombre: " + request.nombre() : info;
        };
    }

    /* =========================================================
     * 3. HERRAMIENTA: Estadísticas (El "Cuántos hay")
     * ========================================================= */
    @Bean
    @Description("Obtiene la cantidad total registrada de personas, socios e instalaciones en el club.")
    public Function<ConsultaVaciaRequest, String> contarEstadisticasTool() {
        return request -> {
            long totalPersonas = personaRepository.count();
            long totalSocios = socioRepository.count();
            long totalInstalaciones = instalacionRepository.count();

            return String.format("Total Personas en sistema: %d. De los cuales Socios: %d. Total Instalaciones: %d.", 
                   totalPersonas, totalSocios, totalInstalaciones);
        };
    }

    /* =========================================================
     * 4. HERRAMIENTA: Instalaciones
     * ========================================================= */
    @Bean
    @Description("Muestra la lista de todas las instalaciones del club (canchas, piletas, salones), su capacidad, estado actual y horarios generales.")
    public Function<ConsultaVaciaRequest, String> consultarInstalacionesTool() {
        return request -> {
            // Acá traés las instalaciones de tu BD
            List<Instalacion> instalaciones = (List<Instalacion>) instalacionRepository.findAll();
            
            String lista = instalaciones.stream()
                .map(i -> i.getNombre() + " (Capacidad: " + i.getCapacidad() + " - Estado: " + i.getEstado() + ")")
                .collect(Collectors.joining(", "));
                
            return "Horarios del club: Lunes a Domingos de 8:00 a 22:00 hs. Instalaciones disponibles: " + lista;
        };
    }

    /* =========================================================
     * 5. HERRAMIENTA: Listar Deudores / Morosos
     * ========================================================= */
    @Bean
    @Description("Obtiene una lista de todas las personas o socios que tienen una deuda mayor a cero. Útil cuando el usuario quiere saber quién debe plata al club.")
    public Function<ConsultaVaciaRequest, String> listarMorososTool() {
        return request -> {
            List<Persona> todas = (List<Persona>) personaRepository.findAll();
            
            String morosos = todas.stream()
                .filter(p -> p.getDeuda() != null && p.getDeuda() > 0) // Validamos que no sea null por las dudas
                .map(p -> "- " + p.getApynom() + " | Debe: $" + p.getDeuda() + " | Contacto: " + p.getEmail())
                .collect(Collectors.joining("\n"));
                
            return morosos.isEmpty() ? 
                   "Excelente noticia: No hay ninguna persona con deudas registradas actualmente. Todos están al día." : 
                   "Lista de personas con deudas pendientes:\n" + morosos;
        };
    }

    /* =========================================================
     * 6. HERRAMIENTA: Actividades, Eventos y Horarios
     * ========================================================= */
    public record ActividadRequest(
            @JsonPropertyDescription("El nombre del deporte o evento a buscar. Si el usuario pregunta en general, DEBES enviar un string vacío '', NUNCA envíes null.") 
            String deporteOEvento
        ) {}

    @Bean
    @Description("Busca información de actividades regulares (con horarios) y eventos especiales (con fecha). Útil para saber qué deportes o eventos hay en el club, precios y cupos.")
    public Function<ActividadRequest, String> consultarActividadesTool() {
        return request -> {
            List<ActividadBase> todas = new ArrayList<>();
            actividadRepository.findAll().forEach(todas::add);
            eventoRepository.findAll().forEach(todas::add);
            
            String filtro = request.deporteOEvento() != null ? request.deporteOEvento().toLowerCase() : "";
            
            String resultado = todas.stream()
                .filter(a -> a.getNombre() != null && a.getNombre().toLowerCase().contains(filtro))
                .map(a -> {
                    if (a instanceof Evento evento) {
                        int inscriptos = evento.getParticipantes() != null ? evento.getParticipantes().size() : 0;
                        return "🎉 EVENTO: " + evento.getNombre() + " | Fecha: " + evento.getFecha() + 
                               " | Cupo: " + inscriptos + "/" + evento.getCapacidad() + " | Precio: $" + evento.getMonto();
                               
                    } else if (a instanceof Actividad actividad) {
                        int inscriptos = actividad.getParticipantes() != null ? actividad.getParticipantes().size() : 0;
                        
                        String horarios = (actividad.getHorarios() != null && !actividad.getHorarios().isEmpty()) 
                                          ? actividad.getHorarios().stream()
                                              .map(h -> h.getDiaSemana() + " de " + h.getHoraInicio() + " a " + h.getHoraFin())
                                              .collect(Collectors.joining(", "))
                                          : "A definir";

                        return "🏅 ACTIVIDAD: " + actividad.getNombre() + " | Horarios: " + horarios + 
                               " | Socios inscriptos: " + inscriptos + " | Precio mensual: $" + actividad.getMonto();
                    }
                    return "- " + a.getNombre();
                })
                .collect(Collectors.joining("\n"));
                
            return resultado.isEmpty() ? 
                   "No encontré actividades ni eventos relacionados con: " + request.deporteOEvento() : 
                   "Esto es lo que encontré en el club:\n" + resultado;
        };
    }
}