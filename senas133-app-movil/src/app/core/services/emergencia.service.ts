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
        {
          tipo,
          subtipo: 'ROBO_CON_VIOLENCIA',
          titulo: 'Robo',
          descripcion: 'Alguien roba o quiere robar',
          icono: 'assets/img/icono-robo.png'
        },
        {
          tipo,
          subtipo: 'ASALTO_EN_CURSO',
          titulo: 'Asalto / Armas',
          descripcion: 'Veo cuchillo o pistola',
          icono: 'assets/img/icono-armas.png'
        },
        {
          tipo,
          subtipo: 'AGRESION_FISICA',
          titulo: 'Golpe / Pelea',
          descripcion: 'Personas golpeando o peleando',
          icono: 'assets/img/icono-golpe-pelea.png'
        }
      ],

      ASISTENCIA_VIAL: [
        {
          tipo,
          subtipo: 'ACCIDENTE_TRANSITO',
          titulo: 'Choque / Accidente',
          descripcion: 'Auto chocó o persona herida',
          icono: 'assets/img/icono-choque.png'
        },
        {
          tipo,
          subtipo: 'PERSONA_SOSPECHOSA',
          titulo: 'Persona me sigue',
          descripcion: 'Alguien me sigue o amenaza',
          icono: 'assets/img/icono-persona-sigue.png'
        },
        {
          tipo,
          subtipo: 'PELEAS_DESORDENES',
          titulo: 'Pelea',
          descripcion: 'Personas peleando o rompiendo cosas',
          icono: 'assets/img/icono-pelea.png'
        }
      ],

      SOSPECHA_PREVENCION: [
        {
          tipo,
          subtipo: 'VEHICULO_SOSPECHOSO',
          titulo: 'Auto extraño',
          descripcion: 'Auto da vueltas',
          icono: 'assets/img/icono-auto-extrano.png'
        },
        {
          tipo,
          subtipo: 'PERSONA_MERODEANDO',
          titulo: 'Persona extraña',
          descripcion: 'Persona mira casas o portones',
          icono: 'assets/img/icono-persona-extrana.png'
        },
        {
          tipo,
          subtipo: 'MARCAJE_CASA',
          titulo: 'Marca en puerta',
          descripcion: 'Marca rara en puerta o reja',
          icono: 'assets/img/icono-marca-puerta.png'
        }
      ]
    } as const;

    return data[tipo];
  }
}
