import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, switchMap, timer } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CamuflajeEmergenciaRequest,
  CamuflajeEmergenciaResponse,
  CamuflajeResultadoResponse,
  PreguntaCamuflaje
} from '../models/camuflaje.model';

@Injectable({ providedIn: 'root' })
export class CamuflajeService {
  private readonly api = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  configuracion(): Observable<{ preguntas: PreguntaCamuflaje[] }> {
    return this.http.get<{ preguntas: PreguntaCamuflaje[] }>(`${this.api}/camuflaje/configuracion`);
  }

  crearEmergencia(request: CamuflajeEmergenciaRequest): Observable<CamuflajeEmergenciaResponse> {
    return this.http.post<CamuflajeEmergenciaResponse>(`${this.api}/camuflaje/emergencias`, request);
  }

  resultado(emergenciaId: number): Observable<CamuflajeResultadoResponse> {
    return this.http.get<CamuflajeResultadoResponse>(`${this.api}/camuflaje/emergencias/${emergenciaId}/resultado`);
  }

  observarResultado(emergenciaId: number): Observable<CamuflajeResultadoResponse> {
    return timer(0, environment.pollingMs).pipe(switchMap(() => this.resultado(emergenciaId)));
  }
}
