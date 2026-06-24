package com.mediaciondirecta.entity;

import com.mediaciondirecta.enums.EstadoSmsContingencia;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_contingencia")
public class SmsContingencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergencia_id")
    private Emergencia emergencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoEmergencia tipo;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SubtipoIncidente subtipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSmsContingencia estado;

    @Column(nullable = false, length = 30)
    private String numeroDestino;

    @Column(nullable = false, length = 500)
    private String textoSms;

    private Double latitud;
    private Double longitud;
    private Double precisionMetros;

    @Column(nullable = false)
    private boolean enviadoPorUsuario;

    @Column(nullable = false)
    private boolean sincronizado;

    @Column(length = 120)
    private String codigoDispositivo;

    private LocalDateTime generadoEnDispositivo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    void prePersist() {
        this.creadoEn = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoSmsContingencia.PREPARADO;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Emergencia getEmergencia() { return emergencia; }
    public void setEmergencia(Emergencia emergencia) { this.emergencia = emergencia; }
    public TipoEmergencia getTipo() { return tipo; }
    public void setTipo(TipoEmergencia tipo) { this.tipo = tipo; }
    public SubtipoIncidente getSubtipo() { return subtipo; }
    public void setSubtipo(SubtipoIncidente subtipo) { this.subtipo = subtipo; }
    public EstadoSmsContingencia getEstado() { return estado; }
    public void setEstado(EstadoSmsContingencia estado) { this.estado = estado; }
    public String getNumeroDestino() { return numeroDestino; }
    public void setNumeroDestino(String numeroDestino) { this.numeroDestino = numeroDestino; }
    public String getTextoSms() { return textoSms; }
    public void setTextoSms(String textoSms) { this.textoSms = textoSms; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public Double getPrecisionMetros() { return precisionMetros; }
    public void setPrecisionMetros(Double precisionMetros) { this.precisionMetros = precisionMetros; }
    public boolean isEnviadoPorUsuario() { return enviadoPorUsuario; }
    public void setEnviadoPorUsuario(boolean enviadoPorUsuario) { this.enviadoPorUsuario = enviadoPorUsuario; }
    public boolean isSincronizado() { return sincronizado; }
    public void setSincronizado(boolean sincronizado) { this.sincronizado = sincronizado; }
    public String getCodigoDispositivo() { return codigoDispositivo; }
    public void setCodigoDispositivo(String codigoDispositivo) { this.codigoDispositivo = codigoDispositivo; }
    public LocalDateTime getGeneradoEnDispositivo() { return generadoEnDispositivo; }
    public void setGeneradoEnDispositivo(LocalDateTime generadoEnDispositivo) { this.generadoEnDispositivo = generadoEnDispositivo; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
