package com.portfolio.club_manager.entities;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "actividades")
@Inheritance(strategy = InheritanceType.JOINED)
public class Actividad extends ActividadBase {

    @JsonManagedReference
    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios = new ArrayList<>();

    @JsonIgnoreProperties("actividades")
    @ManyToMany
    @JoinTable(name = "actividad_socios",
            joinColumns = @JoinColumn(name = "actividad_id"),
            inverseJoinColumns = @JoinColumn(name = "socio_id"))
    private List<Socio> participantes = new ArrayList<>();



    public Actividad() {
    }

    public Actividad(List<Horario> horarios, List<Socio> participantes, String nombre, String descripcion, Double monto, String estado,
            Persona responsable, Instalacion instalacion) {
        super(nombre, descripcion, monto, estado, responsable, instalacion);
        this.horarios = horarios;
        this.participantes = participantes;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }

    public List<Socio> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Socio> participantes) {
        this.participantes = participantes;
    }

}
