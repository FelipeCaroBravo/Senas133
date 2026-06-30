import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClaveUnicaMockResponse, CompletarPerfilRequest, Usuario } from '../models/usuario.model';
import { StorageService } from './storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = environment.apiUrl;

  constructor(private readonly http: HttpClient, private readonly storage: StorageService) {}

  claveUnicaMock(): Observable<ClaveUnicaMockResponse> {
    return this.http.post<ClaveUnicaMockResponse>(`${this.api}/auth/clave-unica/mock`, {}).pipe(
      tap(async response => {
        await this.storage.setUsuario({
          id: response.usuarioId,
          rut: response.rut,
          nombreCompleto: response.nombreCompleto,
          claveUnicaValidada: response.claveUnicaValidada,
          perfilCompleto: response.perfilCompleto
        });
      })
    );
  }

  completarPerfil(usuarioId: number, request: CompletarPerfilRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.api}/usuarios/${usuarioId}/completar-perfil`, request).pipe(
      tap(usuario => this.storage.setUsuario(usuario))
    );
  }

  loginPin(usuarioId: number, pin: string): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.api}/auth/login-pin`, { usuarioId, pin }).pipe(
      tap(usuario => this.storage.setUsuario(usuario))
    );
  }

  getPerfil(usuarioId: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.api}/usuarios/${usuarioId}`);
  }

  actualizarPerfil(usuarioId: number, data: { direccionPrincipal: string; telefono: string }): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.api}/usuarios/${usuarioId}/perfil`, data).pipe(
      tap(usuario => this.storage.setUsuario(usuario))
    );
  }

  cambiarPin(usuarioId: number, pinActual: string, pinNuevo: string): Observable<void> {
    return this.http.patch<void>(`${this.api}/usuarios/${usuarioId}/pin`, { pinActual, pinNuevo });
  }
}
