import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import {
  IonContent,
  IonModal,
  IonIcon,
  ToastController
} from '@ionic/angular/standalone';

import { addIcons } from 'ionicons';
import {
  personCircleOutline,
  playCircleOutline
} from 'ionicons/icons';

import { EmergenciaService } from '../../core/services/emergencia.service';
import { LocationService } from '../../core/services/location.service';
import { StorageService } from '../../core/services/storage.service';
import { TipoEmergencia } from '../../core/models/emergencia.model';

@Component({
  standalone: true,
  selector: 'app-emergencias',
  imports: [
    IonContent,
    CommonModule,
    IonModal,
    IonIcon
  ],
  templateUrl: './emergencias.page.html',
  styleUrl: './emergencias.page.scss'
})
export class EmergenciasPage {
  ayudaAbierta = false;
  enviandoRoja = false;
  sinInternetDemo = false;

get sinInternet(): boolean {
  return localStorage.getItem('sinInternetDemo') === 'true' || !navigator.onLine;
}

get horaActual(): string {
  return new Date().toLocaleTimeString('es-CL', {
    hour: '2-digit',
    minute: '2-digit'
  });
}

alternarInternet(): void {
  this.sinInternetDemo = !this.sinInternetDemo;
  localStorage.setItem('sinInternetDemo', String(this.sinInternetDemo));
}

  constructor(
    private readonly router: Router,
    private readonly storage: StorageService,
    private readonly location: LocationService,
    private readonly emergenciaService: EmergenciaService,
    private readonly toastController: ToastController
  ) {
    addIcons({
      personCircleOutline,
      playCircleOutline
    });
  }

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

    /*
      MODO SIN INTERNET:
      Si el WiFi está en rojo o el navegador detecta que no hay conexión,
      no intentamos mandar por internet. Vamos a pantalla SMS.
    */
    if (this.sinInternet) {
      try {
        const ubicacion = await this.location.getCurrentLocation();

        this.enviandoRoja = false;

        await this.router.navigate(['/sms-emergencia'], {
          queryParams: {
            tipo: 'PELIGRO',
            mensaje: 'Necesito ayuda urgente',
            latitud: ubicacion.latitud,
            longitud: ubicacion.longitud
          }
        });

        return;
      } catch (error) {
        console.error(error);
        this.enviandoRoja = false;

        await this.mostrarToast(
          'No se pudo obtener la ubicación para preparar el SMS.',
          'danger'
        );

        return;
      }
    }

    /*
      MODO CON INTERNET:
      Este es tu flujo normal, no lo cambiamos.
    */
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
        next: async (emergencia: any) => {
          this.enviandoRoja = false;

          const emergenciaId =
            emergencia?.id ??
            emergencia?.emergenciaId ??
            emergencia?.idEmergencia;

          if (!emergenciaId) {
            await this.mostrarToast(
              'La alerta fue creada, pero no se recibió el ID.',
              'warning'
            );
            return;
          }

          await this.router.navigate(['/pregunta-info', emergenciaId]);
        },
        error: async (error) => {
          console.error(error);
          this.enviandoRoja = false;

          await this.mostrarToast(
            'No se pudo enviar la alerta por internet. Puedes usar SMS de contingencia.',
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

  irCamuflaje(): void {
    if (this.enviandoRoja) {
      return;
    }

    this.router.navigateByUrl('/camuflaje');
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