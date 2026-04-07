package com.portfolio.club_manager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Double monto;
    private String concepto; 

    private String metodo;
    
    @Column(updatable = false)
    private LocalDateTime fechaHora = LocalDateTime.now(); 

    @ManyToOne
    @JoinColumn(name = "persona_id")
    @JsonIgnore 
    private Persona persona;

    public Pago() {}

    public Pago(Double monto, String concepto, String metodo, Persona persona) {
        this.monto = monto;
        this.concepto = concepto;
        this.metodo = metodo;
        this.persona = persona;
    }

    public Integer getId() {
        return id;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
}