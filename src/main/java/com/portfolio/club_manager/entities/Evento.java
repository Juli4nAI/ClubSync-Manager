package com.portfolio.club_manager.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@Entity
@Table(name = "eventos")
@PrimaryKeyJoinColumn(name = "id")
public class Evento extends ActividadBase {

    private Integer capacidad;
    private LocalDate fecha;
    private Integer duracion;

    @JsonIgnoreProperties("eventos")
    @ManyToMany
    @JoinTable(
            name = "evento_participantes",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "persona_id"))
    private List<Persona> participantes = new ArrayList<>();

    public Evento() {
    }

    public Evento(String nombre, String descripcion, Double monto, String estado,
            Persona responsable, Instalacion instalacion, List<Persona> participantes,
            Integer capacidad, LocalDate fecha, Integer duracion) {

        super(nombre, descripcion, monto, estado, responsable, instalacion);

        this.capacidad = capacidad;
        this.fecha = fecha;
        this.duracion = duracion;
        this.participantes = participantes;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public List<Persona> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Persona> participantes) {
        this.participantes = participantes;
    }

    @Override
    public String toString() {
        return "Evento [id=" + getId() + ", nombre=" + getNombre() + ", capacidad=" + capacidad + ", fecha=" + fecha
                + "]";
    }
}