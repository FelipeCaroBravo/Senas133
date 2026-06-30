import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { IonContent, IonModal } from '@ionic/angular/standalone';
import { EmergenciaService } from '../../core/services/emergencia.service';
import { LocationService } from '../../core/services/location.service';
import { StorageService } from '../../core/services/storage.service';
import { TipoEmergencia } from '../../core/models/emergencia.model';

@Component({
  standalone: true,
  selector: 'app-emergencias',
  imports: [IonContent, IonModal],
  templateUrl: './emergencias.page.html',
  styleUrl: './emergencias.page.scss'
})
export class EmergenciasPage {
  ayudaAbierta = false;
  enviandoRoja = false;

  constructor(
    private readonly router: Router,
    private readonly storage: StorageService,
    private readonly location: LocationService,
    private readonly emergenciaService: EmergenciaService
  ) {}

  async enviarRojaInmediata(): Promise<void> {
    if (this.enviandoRoja) return;
    this.enviandoRoja = true;

    const usuario = await this.storage.getUsuario();
    if (!usuario?.id) {
      alert('Debes iniciar sesión primero.');
      this.enviandoRoja = false;
      return;
    }

    const ubicacion = await this.location.getCurrentLocation();
    this.emergenciaService.crear({
      usuarioId: usuario.id,
      tipo: 'DELITOS_GRAVES',
      subtipo: null,
      mensaje: 'Alerta silenciosa',
      fraseCodigo: 'ALERTA_SILENCIOSA',
      modoCamuflaje: false,
      requiereInterprete: true,
      canal: 'INTERNET',
      latitud: ubicacion.latitud,
      longitud: ubicacion.longitud,
      precisionMetros: ubicacion.precisionMetros
    }).subscribe({
      next: emergencia => this.router.navigate(['/pregunta-info', emergencia.id]),
      error: async () => {
        this.enviandoRoja = false;
        alert('No se pudo enviar la alerta por internet. Puedes usar SMS de contingencia si está configurado.');
      }
    });
  }

  abrirCategoria(tipo: TipoEmergencia): void {
    this.router.navigate(['/detalle', tipo]);
  }

  perfil(): void {
    this.router.navigateByUrl('/perfil');
  }

  volver(): void {
    this.router.navigateByUrl('/pin');
  }
}
