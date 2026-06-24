package com.mediaciondirecta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas_contexto_emergencia")
public class RespuestaContextoEmergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emergencia_id", nullable = false)
    private Emergencia emergencia;

    @Column(nullable = false, length = 60)
    private String codigoPregunta;

    @Column(nullable = false, length = 180)
    private String textoPregunta;

    @Column(nullable = false)
    private Boolean respuesta;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    private boolean modoCamuflaje;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    void prePersist() {
        this.creadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Emergencia getEmergencia() { return emergencia; }
    public void setEmergencia(Emergencia emergencia) { this.emergencia = emergencia; }
    public String getCodigoPregunta() { return codigoPregunta; }
    public void setCodigoPregunta(String codigoPregunta) { this.codigoPregunta = codigoPregunta; }
    public String getTextoPregunta() { return textoPregunta; }
    public void setTextoPregunta(String textoPregunta) { this.textoPregunta = textoPregunta; }
    public Boolean getRespuesta() { return respuesta; }
    public void setRespuesta(Boolean respuesta) { this.respuesta = respuesta; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public boolean isModoCamuflaje() { return modoCamuflaje; }
    public void setModoCamuflaje(boolean modoCamuflaje) { this.modoCamuflaje = modoCamuflaje; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
