package com.portfolio.club_manager.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "actividad_base")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ActividadBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    private String descripcion;

    private Double monto;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "responsable_id", referencedColumnName = "id")
    private Persona responsable;

    @ManyToOne
    @JoinColumn(name = "instalacion_id", referencedColumnName = "id")
    private Instalacion instalacion;



    public ActividadBase() {
    }

    public ActividadBase(String nombre, String descripcion, Double monto, String estado,
            Persona responsable,
            Instalacion instalacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.monto = monto;
        this.estado = estado;
        this.responsable = responsable;
        this.instalacion = instalacion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public Persona getResponsable() {
        return responsable;
    }

    public void setResponsable(Persona responsable) {
        this.responsable = responsable;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Instalacion getInstalacion() {
        return instalacion;
    }

    public void setInstalacion(Instalacion instalacion) {
        this.instalacion = instalacion;
    }

}
