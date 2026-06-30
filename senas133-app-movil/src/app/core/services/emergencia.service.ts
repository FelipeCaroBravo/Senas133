import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timer, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CrearEmergenciaRequest,
  DetalleEmergenciaRequest,
  Emergencia,
  TipoEmergencia,
  UbicacionRequest
} from '../models/emergencia.model';

@Injectable({ providedIn: 'root' })
export class EmergenciaService {
  private readonly api = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  crear(request: CrearEmergenciaRequest): Observable<Emergencia> {
    return this.http.post<Emergencia>(`${this.api}/emergencias`, request);
  }

  obtener(id: number): Observable<Emergencia> {
    return this.http.get<Emergencia>(`${this.api}/emergencias/${id}`);
  }

  observar(id: number): Observable<Emergencia> {
    return timer(0, environment.pollingMs).pipe(switchMap(() => this.obtener(id)));
  }

  completarDetalle(id: number, request: DetalleEmergenciaRequest): Observable<Emergencia> {
    return this.http.patch<Emergencia>(`${this.api}/emergencias/${id}/detalle`, request);
  }

  actualizarUbicacion(id: number, request: UbicacionRequest): Observable<void> {
    return this.http.post<void>(`${this.api}/emergencias/${id}/ubicacion`, request);
  }

  solicitarInterprete(id: number): Observable<Emergencia> {
    return this.http.post<Emergencia>(`${this.api}/emergencias/${id}/interprete/solicitar`, {});
  }

  prepararSms(request: CrearEmergenciaRequest): Observable<any> {
    return this.http.post(`${this.api}/sms/preparar`, request);
  }

  opcionesPorTipo(tipo: TipoEmergencia) {
    const data = {
      DELITOS_GRAVES: [
        { tipo, subtipo: 'ROBO_CON_VIOLENCIA', titulo: 'Robo con Violencia', descripcion: 'Toque para enviar detalles a la central', icono: '🚨' },
        { tipo, subtipo: 'ASALTO_EN_CURSO', titulo: 'Asalto en Curso', descripcion: 'Toque para enviar detalles a la central', icono: '⚡' },
        { tipo, subtipo: 'AGRESION_FISICA', titulo: 'Agresión Física', descripcion: 'Toque para enviar detalles a la central', icono: '✋' }
      ],
      ASISTENCIA_VIAL: [
        { tipo, subtipo: 'ACCIDENTE_TRANSITO', titulo: 'Accidente de Tránsito', descripcion: 'Toque para enviar detalles a la central', icono: '⚠️' },
        { tipo, subtipo: 'PERSONA_SOSPECHOSA', titulo: 'Persona Sospechosa', descripcion: 'Toque para enviar detalles a la central', icono: '👥' },
        { tipo, subtipo: 'PELEAS_DESORDENES', titulo: 'Peleas / Desórdenes', descripcion: 'Toque para enviar detalles a la central', icono: '🔥' }
      ],
      SOSPECHA_PREVENCION: [
        { tipo, subtipo: 'VEHICULO_SOSPECHOSO', titulo: 'Vehículo Sospechoso', descripcion: 'Toque para enviar detalles a la central', icono: '🚙' },
        { tipo, subtipo: 'PERSONA_MERODEANDO', titulo: 'Persona Merodeando', descripcion: 'Toque para enviar detalles a la central', icono: '👣' },
        { tipo, subtipo: 'MARCAJE_CASA', titulo: 'Marcaje de Casa', descripcion: 'Toque para enviar detalles a la central', icono: '⌂' }
      ]
    } as const;

    return data[tipo];
  }
}
