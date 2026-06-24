package com.mediaciondirecta.entity;

import com.mediaciondirecta.enums.CanalAlerta;
import com.mediaciondirecta.enums.EstadoEmergencia;
import com.mediaciondirecta.enums.SubtipoIncidente;
import com.mediaciondirecta.enums.TipoEmergencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergencias")
public class Emergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoEmergencia tipo;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SubtipoIncidente subtipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoEmergencia estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CanalAlerta canal;

    @Column(length = 250)
    private String mensaje;

    @Column(length = 80)
    private String fraseCodigo;

    @Column(nullable = false)
    private boolean modoCamuflaje;

    @Column(nullable = false)
    private boolean requiereInterprete;

    @Column(length = 120)
    private String codigoExterno;

    private Double latitud;
    private Double longitud;
    private Double precisionMetros;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(nullable = false)
    private LocalDateTime actualizadoEn;

    private LocalDateTime canceladoEn;
    private LocalDateTime cerradoEn;

    @PrePersist
    void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        this.creadoEn = ahora;
        this.actualizadoEn = ahora;
        if (this.estado == null) {
            this.estado = EstadoEmergencia.ENVIADA;
        }
        if (this.canal == null) {
            this.canal = CanalAlerta.INTERNET;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TipoEmergencia getTipo() { return tipo; }
    public void setTipo(TipoEmergencia tipo) { this.tipo = tipo; }
    public SubtipoIncidente getSubtipo() { return subtipo; }
    public void setSubtipo(SubtipoIncidente subtipo) { this.subtipo = subtipo; }
    public EstadoEmergencia getEstado() { return estado; }
    public void setEstado(EstadoEmergencia estado) { this.estado = estado; }
    public CanalAlerta getCanal() { return canal; }
    public void setCanal(CanalAlerta canal) { this.canal = canal; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getFraseCodigo() { return fraseCodigo; }
    public void setFraseCodigo(String fraseCodigo) { this.fraseCodigo = fraseCodigo; }
    public boolean isModoCamuflaje() { return modoCamuflaje; }
    public void setModoCamuflaje(boolean modoCamuflaje) { this.modoCamuflaje = modoCamuflaje; }
    public boolean isRequiereInterprete() { return requiereInterprete; }
    public void setRequiereInterprete(boolean requiereInterprete) { this.requiereInterprete = requiereInterprete; }
    public String getCodigoExterno() { return codigoExterno; }
    public void setCodigoExterno(String codigoExterno) { this.codigoExterno = codigoExterno; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public Double getPrecisionMetros() { return precisionMetros; }
    public void setPrecisionMetros(Double precisionMetros) { this.precisionMetros = precisionMetros; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
    public LocalDateTime getCanceladoEn() { return canceladoEn; }
    public void setCanceladoEn(LocalDateTime canceladoEn) { this.canceladoEn = canceladoEn; }
    public LocalDateTime getCerradoEn() { return cerradoEn; }
    public void setCerradoEn(LocalDateTime cerradoEn) { this.cerradoEn = cerradoEn; }
}
