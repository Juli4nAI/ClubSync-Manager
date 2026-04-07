package com.portfolio.club_manager.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String apynom;

    @Column(unique = true, nullable = false)
    private String dni;

    private LocalDate fechaNacimiento;
    private String sexo;
    private String domicilio;
    private String email;
    private String celular;
    private Double deuda;

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVO'")
    private String estado = "ACTIVO";

    public Persona() {

    }

    public Persona(String apynom, String dni, LocalDate fechaNacimiento, String sexo, String domicilio, String email,
            String celular, Double deuda, String estado) {
        this.apynom = apynom;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.domicilio = domicilio;
        this.email = email;
        this.celular = celular;
        this.deuda = deuda;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApynom() {
        return apynom;
    }

    public void setApynom(String apynom) {
        this.apynom = apynom;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Double getDeuda() {
        return deuda;
    }

    public void setDeuda(Double deuda) {
        this.deuda = deuda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
