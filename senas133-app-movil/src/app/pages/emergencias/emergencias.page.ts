import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {
  IonContent,
  IonModal,
  ToastController
} from '@ionic/angular/standalone';

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
    private readonly emergenciaService: EmergenciaService,
    private readonly toastController: ToastController
  ) {}

  async enviarRojaInmediata(): Promise<void> {
    if (this.enviandoRoja) {
      return;
    }

    this.enviandoRoja = true;

    const usuario = await this.storage.getUsuario();

    if (!usuario?.id) {
      this.enviandoRoja = false;

      await this.mostrarToast(
        'Debes iniciar sesión primero.',
        'warning'
      );

      await this.router.navigateByUrl('/inicio');
      return;
    }

    try {
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
        next: async (emergencia) => {
          this.enviandoRoja = false;
          await this.router.navigate(['/pregunta-info', emergencia.id]);
        },
        error: async (error) => {
          console.error(error);
          this.enviandoRoja = false;

          await this.mostrarToast(
            'No se pudo enviar la alerta por internet. Puedes usar SMS de contingencia si está configurado.',
            'danger'
          );
        }
      });
    } catch (error) {
      console.error(error);
      this.enviandoRoja = false;

      await this.mostrarToast(
        'No se pudo obtener la ubicación del dispositivo.',
        'danger'
      );
    }
  }

  abrirCategoria(tipo: TipoEmergencia): void {
    if (this.enviandoRoja) {
      return;
    }

    this.router.navigate(['/detalle', tipo]);
  }

  perfil(): void {
    if (this.enviandoRoja) {
      return;
    }

    this.router.navigateByUrl('/perfil');
  }

  volver(): void {
    if (this.enviandoRoja) {
      return;
    }

    this.router.navigateByUrl('/pin');
  }

  private async mostrarToast(
    mensaje: string,
    color: 'danger' | 'warning' | 'success' | 'primary' = 'primary'
  ): Promise<void> {
    const toast = await this.toastController.create({
      message: mensaje,
      duration: 3000,
      position: 'top',
      color,
      buttons: [
        {
          text: 'OK',
          role: 'cancel'
        }
      ]
    });

    await toast.present();
  }
}
