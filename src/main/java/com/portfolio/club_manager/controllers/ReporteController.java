package com.portfolio.club_manager.controllers;

import com.portfolio.club_manager.dtos.MorosoDTO;
import com.portfolio.club_manager.entities.*;
import com.portfolio.club_manager.repositories.*;
import com.portfolio.club_manager.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {    

    
    private final GastoService gastoService;
    private final SocioRepository socioRepository;
    private final ActividadRepository actividadRepository;
    private final EventoRepository eventoRepository;
    private final InstalacionRepository instalacionRepository;
    private final PagoRepository pagoRepository; 

    public ReporteController(GastoService gastoService, 
                             SocioRepository socioRepository, ActividadRepository actividadRepository, 
                             EventoRepository eventoRepository, InstalacionRepository instalacionRepository, PagoRepository pagoRepository) {
        this.gastoService = gastoService;
        this.socioRepository = socioRepository;
        this.actividadRepository = actividadRepository;
        this.eventoRepository = eventoRepository;
        this.instalacionRepository = instalacionRepository;
        this.pagoRepository = pagoRepository;
    }

    

    
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> obtenerBalance(@RequestParam Map<String, String> parametros) {
        try {
            String inicioStr = parametros.get("inicio");
            String finStr = parametros.get("fin");

            
            LocalDate inicioDate = LocalDate.parse(inicioStr);
            LocalDate finDate = LocalDate.parse(finStr);

            
            LocalDateTime inicioDateTime = inicioDate.atStartOfDay(); 
            LocalDateTime finDateTime = finDate.atTime(LocalTime.MAX); 

            
            Double ingresos = pagoRepository.sumarPagosEntreFechas(inicioDateTime, finDateTime);
            
            
            Double gastos = gastoService.calcularGastosEntreFechas(inicioDate, finDate);

            
            ingresos = (ingresos != null) ? ingresos : 0.0;
            gastos = (gastos != null) ? gastos : 0.0;

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("periodo", "Desde " + inicioDate + " hasta " + finDate);
            respuesta.put("totalIngresos", ingresos);
            respuesta.put("totalGastos", gastos);
            respuesta.put("saldoFinal", ingresos - gastos);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error interno procesando el balance: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/morosos")
    public ResponseEntity<List<MorosoDTO>> listarMorosos() {
        
        List<MorosoDTO> morosos = socioRepository.buscarMorosos();
        return ResponseEntity.ok(morosos);
    }
     

    
    @GetMapping("/actividades-inscriptos")
    public ResponseEntity<Map<String, Integer>> cantidadInscriptosActividad() {
        List<Object[]> resultados = actividadRepository.obtenerConteoInscriptos();
        Map<String, Integer> reporte = new HashMap<>();
        for (Object[] fila : resultados) {
            reporte.put((String) fila[0], ((Number) fila[1]).intValue());
        }
        return ResponseEntity.ok(reporte);
    }
 
    @GetMapping("/evento/{id}/participantes")
    public ResponseEntity<List<Persona>> obtenerParticipantesEvento(@PathVariable Long id) {
        
        List<Persona> participantes = eventoRepository.listarParticipantesEvento(id);
        
        return ResponseEntity.ok(participantes);
    }
    

    
    @GetMapping("/recaudacion-por-tipo")
    public ResponseEntity<Map<String, Double>> obtenerRecaudacionPorTipo() {
        
        
        List<Object[]> resultados = pagoRepository.sumarRecaudacionPorTipo();
        Map<String, Double> reporte = new HashMap<>();
        
        
        for (Object[] fila : resultados) {
            
            String tipo = fila[0] != null ? fila[0].toString() : "Sin Especificar";
            Double monto = fila[1] != null ? (Double) fila[1] : 0.0;
            
            reporte.put(tipo, monto);
        }
        
        return ResponseEntity.ok(reporte);
    }

    
    @GetMapping("/ocupacion-instalaciones")
    public ResponseEntity<Map<String, Long>> obtenerOcupacionInstalaciones() {
        List<Object[]> resultados = instalacionRepository.contarActividadesPorInstalacion();
        Map<String, Long> reporte = new HashMap<>();
        for (Object[] fila : resultados) {
            reporte.put((String) fila[0], (Long) fila[1]);
        }
        return ResponseEntity.ok(reporte);
    }

    
    @GetMapping("/estado-socios")
    public ResponseEntity<Map<String, Long>> obtenerEstadoSocios() {
        
        List<Object[]> resultados = socioRepository.contarSociosPorEstado();
        
        Map<String, Long> reporte = new HashMap<>();

        for (Object[] fila : resultados) {
            
            String estado = fila[0].toString();
            Long cantidad = (Long) fila[1];
            reporte.put(estado, cantidad);
        }

        return ResponseEntity.ok(reporte);
    }
}