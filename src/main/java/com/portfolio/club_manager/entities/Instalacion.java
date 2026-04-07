package com.portfolio.club_manager.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "instalaciones")
public class Instalacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "estado")
    private String estado = "DISPONIBLE";

    private String nombre;

    private String descripcion;

    private Integer largo;
    private Integer ancho;

    @Column(nullable = false)
    private Integer capacidad;

    public Instalacion() {
    }

    public Instalacion(String nombre, String descripcion, Integer largo, Integer ancho, Integer capacidad, String estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.largo = largo;
        this.ancho = ancho;
        this.capacidad = capacidad;
        this.estado = "DISPONIBLE";
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

    public Integer getLargo() {
        return largo;
    }

    public void setLargo(Integer largo) {
        this.largo = largo;
    }

    public Integer getAncho() {
        return ancho;
    }

    public void setAncho(Integer ancho) {
        this.ancho = ancho;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
