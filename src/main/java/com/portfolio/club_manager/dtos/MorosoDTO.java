package com.portfolio.club_manager.dtos;

public class MorosoDTO {
    private Integer socioId; 
    private String nombreCompleto;
    private String dni;     
    private Double deudaTotal;

    public MorosoDTO(Integer socioId, String nombreCompleto, String dni, Double deudaTotal) {
        this.socioId = socioId;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.deudaTotal = deudaTotal;
    }

    
    public Integer getSocioId() { return socioId; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getDni() { return dni; }
    public Double getDeudaTotal() { return deudaTotal; }

    
    public void setSocioId(Integer socioId) { this.socioId = socioId; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setDni(String dni) { this.dni = dni; }
    public void setDeudaTotal(Double deudaTotal) { this.deudaTotal = deudaTotal; }
}
    

