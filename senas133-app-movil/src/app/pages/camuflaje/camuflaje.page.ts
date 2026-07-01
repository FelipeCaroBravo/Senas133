import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import {
  IonContent,
  ToastController
} from '@ionic/angular/standalone';

import {
  PreguntaCamuflaje,
  RespuestaCamuflajeRequest
} from '../../core/models/camuflaje.model';

import { CamuflajeService } from '../../core/services/camuflaje.service';
import { LocationService } from '../../core/services/location.service';
import { StorageService } from '../../core/services/storage.service';

@Component({
  standalone: true,
  selector: 'app-camuflaje',
  imports: [IonContent, NgIf],
  templateUrl: './camuflaje.page.html',
  styleUrl: './camuflaje.page.scss'
})
export class CamuflajePage {
  piezaColocada = false;

  preguntas: PreguntaCamuflaje[] = [
    {
      codigo: 'AGRESOR_CERCA',
      textoCamuflado: '¿Hay un enemigo cerca?'
    },
    {
      codigo: 'HAY_ARMA',
      textoCamuflado: '¿El enemigo tiene objeto peligroso?'
    },
    {
      codigo: 'NECESITA_AYUDA_INMEDIATA',
      textoCamuflado: '¿Necesitas pasar al siguiente nivel ahora?'
    }
  ];

  respuestas: RespuestaCamuflajeRequest[] = [];
  indice = 0;
  enviando = false;

  constructor(
    private readonly router: Router,
    private readonly camuflajeService: CamuflajeService,
    private readonly location: LocationService,
    private readonly storage: StorageService,
    private readonly toastController: ToastController
  ) {}

  ionViewWillEnter(): void {
    this.piezaColocada = false;
    this.respuestas = [];
    this.indice = 0;
    this.enviando = false;

    this.camuflajeService.configuracion().subscribe({
      next: (config) => {
        if (config.preguntas?.length) {
          this.preguntas = config.preguntas;
        }
      },
      error: async (error) => {
        console.error(error);

        await this.mostrarToast(
          'No se pudo cargar la configuración del modo camuflaje. Se usarán preguntas locales.',
          'warning'
        );
      }
    });
  }

  permitirSoltar(event: DragEvent): void {
    event.preventDefault();
  }

  colocarPieza(): void {
    if (this.enviando) {
      return;
    }

    this.piezaColocada = true;
  }

  responder(respuesta: boolean): void {
    if (this.enviando) {
      return;
    }

    const pregunta = this.preguntas[this.indice];

    if (!pregunta) {
      return;
    }

    this.respuestas.push({
      codigoPregunta: pregunta.codigo,
      respuesta
    });

    if (this.indice < this.preguntas.length - 1) {
      this.indice++;
      return;
    }

    this.enviarAlertaCamuflada();
  }

  async enviarAlertaCamuflada(): Promise<void> {
    if (this.enviando) {
      return;
    }

    this.enviando = true;

    const usuario = await this.storage.getUsuario();

    if (!usuario?.id) {
      this.enviando = false;

      await this.mostrarToast(
        'Debes iniciar sesión antes de usar el modo camuflaje.',
        'warning'
      );

      await this.router.navigateByUrl('/inicio');
      return;
    }

    try {
      const ubicacion = await this.location.getCurrentLocation();

      this.camuflajeService.crearEmergencia({
        usuarioId: usuario.id,
        latitud: ubicacion.latitud,
        longitud: ubicacion.longitud,
        precisionMetros: ubicacion.precisionMetros,
        respuestas: this.respuestas
      }).subscribe({
        next: async (response) => {
          this.enviando = false;
          await this.router.navigate([
            '/camuflaje-resultado',
            response.emergenciaId
          ]);
        },
        error: async (error) => {
          console.error(error);
          this.enviando = false;

          await this.mostrarToast(
            'No se pudo enviar la alerta camuflada.',
            'danger'
          );
        }
      });
    } catch (error) {
      console.error(error);
      this.enviando = false;

      await this.mostrarToast(
        'No se pudo obtener la ubicación del dispositivo.',
        'danger'
      );
    }
  }

  volver(): void {
    if (this.enviando) {
      return;
    }

    this.router.navigateByUrl('/inicio');
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