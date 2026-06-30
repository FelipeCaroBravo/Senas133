import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  Emergencia,
  EstadoEmergencia,
  RespuestaContexto,
  SolicitudInterprete,
  UbicacionEmergencia
} from '../models/emergencia.model';

@Injectable({ providedIn: 'root' })
export class CentralService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  listarEmergenciasActivas(): Observable<Emergencia[]> {
    return this.http.get<Emergencia[]>(`${this.apiUrl}/central/emergencias/activas`);
  }

  obtenerEmergencia(id: number): Observable<Emergencia> {
    return this.http.get<Emergencia>(`${this.apiUrl}/central/emergencias/${id}`);
  }

  cambiarEstado(id: number, estado: EstadoEmergencia): Observable<Emergencia> {
    return this.http.patch<Emergencia>(`${this.apiUrl}/central/emergencias/${id}/estado`, { estado });
  }

  listarUbicaciones(id: number): Observable<UbicacionEmergencia[]> {
    return this.http.get<UbicacionEmergencia[]>(`${this.apiUrl}/central/emergencias/${id}/ubicaciones`);
  }

  listarContextoCamuflaje(id: number): Observable<RespuestaContexto[]> {
    return this.http.get<RespuestaContexto[]>(`${this.apiUrl}/central/emergencias/${id}/contexto`);
  }

  listarInterpretesPendientes(): Observable<SolicitudInterprete[]> {
    return this.http.get<SolicitudInterprete[]>(`${this.apiUrl}/central/interpretes/pendientes`);
  }

  asignarInterprete(emergenciaId: number, nombreInterprete: string, urlSalaVideo: string): Observable<SolicitudInterprete> {
    return this.http.patch<SolicitudInterprete>(`${this.apiUrl}/central/emergencias/${emergenciaId}/interprete/asignar`, {
      nombreInterprete,
      urlSalaVideo
    });
  }

  iniciarLlamada(emergenciaId: number): Observable<SolicitudInterprete> {
    return this.http.patch<SolicitudInterprete>(`${this.apiUrl}/central/emergencias/${emergenciaId}/interprete/iniciar-llamada`, {});
  }

  abrirStream(onEvent: () => void, onStatus?: (connected: boolean) => void): EventSource {
    const source = new EventSource(`${this.apiUrl}/central/stream`);

    const eventos = [
      'conectado',
      'nueva_emergencia',
      'emergencia_actualizada',
      'ubicacion_actualizada',
      'estado_actualizado',
      'detalle_actualizado',
      'lugar_seguro_actualizado',
      'contexto_camuflaje_actualizado',
      'interprete_solicitado',
      'interprete_asignado',
      'videollamada_iniciada',
      'emergencia_cancelada'
    ];

    source.onopen = () => onStatus?.(true);
    source.onerror = () => onStatus?.(false);

    eventos.forEach(nombreEvento => {
      source.addEventListener(nombreEvento, () => {
        if (nombreEvento === 'conectado') {
          onStatus?.(true);
        }
        onEvent();
      });
    });

    return source;
  }
}
