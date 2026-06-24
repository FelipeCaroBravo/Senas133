package com.mediaciondirecta.entity;

import com.mediaciondirecta.enums.EstadoSolicitudInterprete;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_interprete")
public class SolicitudInterprete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emergencia_id", nullable = false, unique = true)
    private Emergencia emergencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitudInterprete estado;

    @Column(length = 120)
    private String nombreInterprete;

    @Column(length = 300)
    private String urlSalaVideo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        this.creadoEn = ahora;
        this.actualizadoEn = ahora;
        if (this.estado == null) {
            this.estado = EstadoSolicitudInterprete.SOLICITADO;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Emergencia getEmergencia() { return emergencia; }
    public void setEmergencia(Emergencia emergencia) { this.emergencia = emergencia; }
    public EstadoSolicitudInterprete getEstado() { return estado; }
    public void setEstado(EstadoSolicitudInterprete estado) { this.estado = estado; }
    public String getNombreInterprete() { return nombreInterprete; }
    public void setNombreInterprete(String nombreInterprete) { this.nombreInterprete = nombreInterprete; }
    public String getUrlSalaVideo() { return urlSalaVideo; }
    public void setUrlSalaVideo(String urlSalaVideo) { this.urlSalaVideo = urlSalaVideo; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
