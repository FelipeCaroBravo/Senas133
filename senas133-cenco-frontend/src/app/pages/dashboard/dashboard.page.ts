import { AfterViewInit, Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  IonBadge,
  IonButton,
  IonButtons,
  IonCard,
  IonCardContent,
  IonChip,
  IonContent,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonMenuButton,
  IonSegment,
  IonSegmentButton,
  IonSpinner,
  IonTitle,
  IonToolbar,
  ToastController
} from '@ionic/angular/standalone';
import {
  alertCircleOutline,
  carSportOutline,
  checkmarkCircleOutline,
  closeCircleOutline,
  eyeOutline,
  locationOutline,
  mapOutline,
  peopleOutline,
  personCircleOutline,
  refreshOutline,
  shieldCheckmarkOutline,
  videocamOutline,
  warningOutline
} from 'ionicons/icons';
import { addIcons } from 'ionicons';
import * as L from 'leaflet';

import { environment } from '../../../environments/environment';
import { CentralService } from '../../core/services/central.service';
import {
  Emergencia,
  EstadoEmergencia,
  EstadisticasDashboard,
  RespuestaContexto,
  SolicitudInterprete,
  TipoEmergencia,
  UbicacionEmergencia
} from '../../core/models/emergencia.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    IonBadge,
    IonButton,
    IonButtons,
    IonCard,
    IonCardContent,
    IonChip,
    IonContent,
    IonHeader,
    IonIcon,
    IonInput,
    IonItem,
    IonLabel,
    IonList,
    IonSegment,
    IonSegmentButton,
    IonSpinner,
    IonTitle,
    IonToolbar
  ],
  templateUrl: './dashboard.page.html',
  styleUrls: ['./dashboard.page.scss']
})
export class DashboardPage implements OnInit, AfterViewInit, OnDestroy {
  emergencias: Emergencia[] = [];
  emergenciasFiltradas: Emergencia[] = [];
  emergenciaSeleccionada: Emergencia | null = null;
  ubicacionesSeleccionadas: UbicacionEmergencia[] = [];
  contextoCamuflaje: RespuestaContexto[] = [];
  interpretesPendientes: SolicitudInterprete[] = [];

  cargando = false;
  error = '';
  conectadoStream = false;
  filtro: 'TODAS' | EstadoEmergencia = 'TODAS';
  textoBusqueda = '';

  nombreInterprete = 'Intérprete LSCh Demo';
  urlSalaVideo = 'https://meet.google.com/demo-senas133';

  private mapa: L.Map | null = null;
  private marcadores: L.LayerGroup | null = null;
  private rutaUbicaciones: L.Polyline | null = null;
  private eventSource: EventSource | null = null;
  private pollingId: number | null = null;

  constructor(
    private readonly centralService: CentralService,
    private readonly zone: NgZone,
    private readonly toastController: ToastController
  ) {
    addIcons({
      alertCircleOutline,
      carSportOutline,
      checkmarkCircleOutline,
      closeCircleOutline,
      eyeOutline,
      locationOutline,
      mapOutline,
      peopleOutline,
      personCircleOutline,
      refreshOutline,
      shieldCheckmarkOutline,
      videocamOutline,
      warningOutline
    });
  }

  ngOnInit(): void {
    this.cargarTodo();
    this.iniciarStream();
    this.iniciarPollingRespaldo();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.inicializarMapa(), 250);
  }

  ngOnDestroy(): void {
    this.eventSource?.close();
    if (this.pollingId !== null) {
      window.clearInterval(this.pollingId);
    }
    this.mapa?.remove();
  }

  get estadisticas(): EstadisticasDashboard {
    return {
      totalActivas: this.emergencias.length,
      sinConfirmar: this.emergencias.filter(e => e.estado === 'ENVIADA' || e.estado === 'ACTIVA').length,
      patrullasDespachadas: this.emergencias.filter(e => e.estado === 'PATRULLA_DESPACHADA').length,
      camufladas: this.emergencias.filter(e => e.modoCamuflaje).length
    };
  }

  cargarTodo(mostrarCarga = true): void {
    if (mostrarCarga) {
      this.cargando = true;
    }
    this.error = '';

    this.centralService.listarEmergenciasActivas().subscribe({
      next: emergencias => {
        this.emergencias = this.ordenarEmergencias(emergencias);
        this.aplicarFiltros();

        if (!this.emergenciaSeleccionada && this.emergencias.length > 0) {
          this.seleccionarEmergencia(this.emergencias[0], false);
        } else if (this.emergenciaSeleccionada) {
          const actualizada = this.emergencias.find(e => e.id === this.emergenciaSeleccionada?.id);
          if (actualizada) {
            this.emergenciaSeleccionada = actualizada;
            this.cargarDatosSeleccionados(actualizada.id);
          }
        }

        this.actualizarMapa();
        this.cargarInterpretesPendientes();
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudo conectar con el backend. Verifica que Spring Boot esté ejecutándose en el puerto configurado.';
        this.cargando = false;
      }
    });
  }

  refrescar(): void {
    this.cargarTodo();
  }

  seleccionarEmergencia(emergencia: Emergencia, centrar = true): void {
    this.emergenciaSeleccionada = emergencia;
    this.cargarDatosSeleccionados(emergencia.id);
    if (centrar) {
      this.centrarEmergencia(emergencia);
    }
  }

  cambiarEstado(estado: EstadoEmergencia): void {
    const emergencia = this.emergenciaSeleccionada;
    if (!emergencia) return;

    this.centralService.cambiarEstado(emergencia.id, estado).subscribe({
      next: actualizada => {
        this.emergenciaSeleccionada = actualizada;
        this.reemplazarEmergencia(actualizada);
        this.aplicarFiltros();
        this.actualizarMapa();
        this.mostrarToast(`Estado actualizado a ${this.formatearEstado(estado)}.`);
      },
      error: () => this.mostrarToast('No se pudo actualizar el estado.', 'danger')
    });
  }

  asignarInterprete(): void {
    const emergencia = this.emergenciaSeleccionada;
    if (!emergencia) return;

    this.centralService.asignarInterprete(emergencia.id, this.nombreInterprete, this.urlSalaVideo).subscribe({
      next: () => {
        this.mostrarToast('Intérprete asignado correctamente.');
        this.cargarInterpretesPendientes();
        this.cargarTodo(false);
      },
      error: () => this.mostrarToast('No se pudo asignar intérprete.', 'danger')
    });
  }

  iniciarLlamada(): void {
    const emergencia = this.emergenciaSeleccionada;
    if (!emergencia) return;

    this.centralService.iniciarLlamada(emergencia.id).subscribe({
      next: () => {
        this.mostrarToast('Videollamada marcada como iniciada.');
        this.cargarTodo(false);
      },
      error: () => this.mostrarToast('No se pudo iniciar la videollamada.', 'danger')
    });
  }

  aplicarFiltros(): void {
    const busqueda = this.textoBusqueda.trim().toLowerCase();

    this.emergenciasFiltradas = this.emergencias.filter(emergencia => {
      const coincideEstado = this.filtro === 'TODAS' || emergencia.estado === this.filtro;
      const texto = [
        emergencia.id,
        emergencia.nombreUsuario,
        emergencia.telefonoUsuario,
        emergencia.direccionPrincipalUsuario,
        emergencia.tipo,
        emergencia.subtipo,
        emergencia.estado,
        emergencia.mensaje
      ].join(' ').toLowerCase();
      return coincideEstado && (!busqueda || texto.includes(busqueda));
    });
  }

  claseTipo(tipo: TipoEmergencia): string {
    if (tipo === 'DELITOS_GRAVES') return 'red';
    if (tipo === 'ASISTENCIA_VIAL') return 'orange';
    if (tipo === 'SOSPECHA_PREVENCION') return 'blue';
    return 'gray';
  }

  iconoTipo(tipo: TipoEmergencia): string {
    if (tipo === 'DELITOS_GRAVES') return 'shield-checkmark-outline';
    if (tipo === 'ASISTENCIA_VIAL') return 'car-sport-outline';
    if (tipo === 'SOSPECHA_PREVENCION') return 'eye-outline';
    return 'alert-circle-outline';
  }

  formatearTipo(tipo: TipoEmergencia): string {
    const mapa: Record<TipoEmergencia, string> = {
      DELITOS_GRAVES: 'Delitos graves',
      ASISTENCIA_VIAL: 'Asistencia / Vial',
      SOSPECHA_PREVENCION: 'Sospecha y prevención'
    };
    return mapa[tipo] ?? tipo;
  }

  formatearEstado(estado: EstadoEmergencia): string {
    return estado.replaceAll('_', ' ').toLowerCase().replace(/(^|\s)\S/g, letra => letra.toUpperCase());
  }

  formatearSubtipo(subtipo: string | null): string {
    if (!subtipo) return 'Sin detalle específico';
    return subtipo.replaceAll('_', ' ').toLowerCase().replace(/(^|\s)\S/g, letra => letra.toUpperCase());
  }

  fechaCorta(fechaIso: string | null): string {
    if (!fechaIso) return 'No registrado';
    return new Date(fechaIso).toLocaleString('es-CL', {
      hour: '2-digit',
      minute: '2-digit',
      day: '2-digit',
      month: '2-digit'
    });
  }

  private iniciarStream(): void {
    this.eventSource = this.centralService.abrirStream(
      () => this.zone.run(() => this.cargarTodo(false)),
      conectado => this.zone.run(() => (this.conectadoStream = conectado))
    );
  }

  private iniciarPollingRespaldo(): void {
    this.pollingId = window.setInterval(() => this.cargarTodo(false), 8000);
  }

  private inicializarMapa(): void {
    if (this.mapa) return;

    this.mapa = L.map('mapa-cenco', {
      zoomControl: true
    }).setView([environment.defaultMapCenter.lat, environment.defaultMapCenter.lng], environment.defaultZoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.mapa);

    this.marcadores = L.layerGroup().addTo(this.mapa);
    this.actualizarMapa();
  }

  private actualizarMapa(): void {
    if (!this.mapa || !this.marcadores) return;

    this.marcadores.clearLayers();
    const puntos: L.LatLngExpression[] = [];

    for (const emergencia of this.emergencias) {
      if (emergencia.latitud == null || emergencia.longitud == null) continue;

      const latLng: L.LatLngExpression = [emergencia.latitud, emergencia.longitud];
      puntos.push(latLng);

      const icon = L.divIcon({
        className: '',
        html: `<div class="alert-marker ${this.claseTipo(emergencia.tipo)}"></div>`,
        iconSize: [32, 32],
        iconAnchor: [16, 16]
      });

      const marker = L.marker(latLng, { icon })
        .bindPopup(`
          <strong>Alerta #${emergencia.id}</strong><br>
          ${this.formatearTipo(emergencia.tipo)}<br>
          ${this.formatearEstado(emergencia.estado)}<br>
          Precisión: ${emergencia.precisionMetros ?? 'N/D'} m
        `)
        .on('click', () => this.zone.run(() => this.seleccionarEmergencia(emergencia, false)));

      this.marcadores.addLayer(marker);
    }

    this.dibujarRutaSeleccionada();

    if (puntos.length > 1) {
      this.mapa.fitBounds(L.latLngBounds(puntos), { padding: [40, 40] });
    } else if (puntos.length === 1) {
      this.mapa.setView(puntos[0], 15);
    }
  }

  private cargarDatosSeleccionados(id: number): void {
    this.centralService.listarUbicaciones(id).subscribe({
      next: ubicaciones => {
        this.ubicacionesSeleccionadas = ubicaciones;
        this.dibujarRutaSeleccionada();
      },
      error: () => (this.ubicacionesSeleccionadas = [])
    });

    const seleccionada = this.emergenciaSeleccionada;
    if (seleccionada?.modoCamuflaje) {
      this.centralService.listarContextoCamuflaje(id).subscribe({
        next: contexto => (this.contextoCamuflaje = contexto),
        error: () => (this.contextoCamuflaje = [])
      });
    } else {
      this.contextoCamuflaje = [];
    }
  }

  private dibujarRutaSeleccionada(): void {
    if (!this.mapa) return;

    if (this.rutaUbicaciones) {
      this.rutaUbicaciones.removeFrom(this.mapa);
      this.rutaUbicaciones = null;
    }

    const puntos = this.ubicacionesSeleccionadas
      .filter(ubicacion => ubicacion.latitud != null && ubicacion.longitud != null)
      .map(ubicacion => [ubicacion.latitud, ubicacion.longitud] as L.LatLngExpression);

    if (puntos.length > 1) {
      this.rutaUbicaciones = L.polyline(puntos, {
        weight: 4,
        opacity: 0.75,
        dashArray: '6, 8'
      }).addTo(this.mapa);
    }
  }

  private centrarEmergencia(emergencia: Emergencia): void {
    if (!this.mapa || emergencia.latitud == null || emergencia.longitud == null) return;
    this.mapa.setView([emergencia.latitud, emergencia.longitud], 16, { animate: true });
  }

  private reemplazarEmergencia(actualizada: Emergencia): void {
    this.emergencias = this.ordenarEmergencias(this.emergencias.map(e => (e.id === actualizada.id ? actualizada : e)));
  }

  private ordenarEmergencias(emergencias: Emergencia[]): Emergencia[] {
    return [...emergencias].sort((a, b) => new Date(b.creadoEn).getTime() - new Date(a.creadoEn).getTime());
  }

  private cargarInterpretesPendientes(): void {
    this.centralService.listarInterpretesPendientes().subscribe({
      next: solicitudes => (this.interpretesPendientes = solicitudes),
      error: () => (this.interpretesPendientes = [])
    });
  }

  private async mostrarToast(message: string, color: 'success' | 'danger' | 'warning' = 'success'): Promise<void> {
    const toast = await this.toastController.create({
      message,
      duration: 2200,
      color,
      position: 'top'
    });
    await toast.present();
  }
}
