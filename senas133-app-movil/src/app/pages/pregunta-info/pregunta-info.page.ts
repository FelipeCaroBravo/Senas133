import { Component, OnDestroy } from '@angular/core';
import { NgClass } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import {
  IonContent,
  ToastController
} from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';

import { EmergenciaService } from '../../core/services/emergencia.service';
import { Emergencia } from '../../core/models/emergencia.model';

@Component({
  standalone: true,
  selector: 'app-pregunta-info',
  imports: [
    IonContent,
    NgClass
  ],
  templateUrl: './pregunta-info.page.html',
  styleUrl: './pregunta-info.page.scss'
})
export class PreguntaInfoPage implements OnDestroy {
  emergenciaId?: number;
  emergencia?: Emergencia;

  private subscription?: Subscription;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly emergenciaService: EmergenciaService,
    private readonly toastController: ToastController
  ) {}

  async ionViewWillEnter(): Promise<void> {
    const id = Number(this.route.snapshot.paramMap.get('emergenciaId'));

    if (!id) {
      await this.mostrarToast(
        'No se encontró la alerta activa.',
        'warning'
      );

      await this.router.navigateByUrl('/emergencias');
      return;
    }

    this.emergenciaId = id;

    this.subscription?.unsubscribe();

    this.subscription = this.emergenciaService.observar(id).subscribe({
      next: (emergencia) => {
        this.emergencia = emergencia;
      },
      error: async (error) => {
        console.error(error);

        await this.mostrarToast(
          'No se pudo actualizar el estado de la central.',
          'warning'
        );
      }
    });
  }

  ionViewWillLeave(): void {
    this.subscription?.unsubscribe();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  get mensajeCentral(): string {
    const estado = this.emergencia?.estado ?? 'ENVIADA';

    switch (estado) {
      case 'ENVIADA':
        return 'Esperando confirmación de la central';

      case 'RECIBIDA':
        return 'Alerta recibida por la central';

      case 'EN_ATENCION':
        return 'Central revisando tu emergencia';

      case 'PATRULLA_DESPACHADA':
        return 'Patrulla despachada hacia tu ubicación';

      case 'INTERPRETE_SOLICITADO':
        return 'Intérprete solicitado';

      case 'INTERPRETE_CONECTADO':
        return 'Intérprete conectado';

      case 'CERRADA':
        return 'Alerta cerrada por la central';

      case 'CANCELADA':
        return 'Alerta cancelada';

      default:
        return 'Esperando actualización de la central';
    }
  }

  get iconoCentral(): string {
    const estado = this.emergencia?.estado ?? 'ENVIADA';

    switch (estado) {
      case 'ENVIADA':
        return '⏳';

      case 'RECIBIDA':
        return '✅';

      case 'EN_ATENCION':
        return '👮';

      case 'PATRULLA_DESPACHADA':
        return '🚓';

      case 'INTERPRETE_SOLICITADO':
        return '🤟';

      case 'INTERPRETE_CONECTADO':
        return '🎥';

      case 'CERRADA':
        return '✅';

      case 'CANCELADA':
        return '⚠️';

      default:
        return '⏳';
    }
  }

  get claseEstadoCentral(): string {
    const estado = this.emergencia?.estado ?? 'ENVIADA';

    if (estado === 'RECIBIDA') {
      return 'recibida';
    }

    if (estado === 'EN_ATENCION') {
      return 'atencion';
    }

    if (estado === 'PATRULLA_DESPACHADA') {
      return 'patrulla';
    }

    if (
      estado === 'INTERPRETE_SOLICITADO' ||
      estado === 'INTERPRETE_CONECTADO'
    ) {
      return 'interprete';
    }

    if (estado === 'CERRADA') {
      return 'cerrada';
    }

    if (estado === 'CANCELADA') {
      return 'cancelada';
    }

    return 'esperando';
  }

  async responderNo(): Promise<void> {
    if (!this.emergenciaId) {
      await this.router.navigateByUrl('/emergencias');
      return;
    }

    await this.router.navigate([
      '/alerta-activa',
      this.emergenciaId
    ]);
  }

  async responderSi(): Promise<void> {
    if (!this.emergenciaId) {
      await this.router.navigateByUrl('/emergencias');
      return;
    }

    await this.router.navigate(
      ['/detalle', 'DELITOS_GRAVES'],
      {
        queryParams: {
          emergenciaId: this.emergenciaId
        }
      }
    );
  }

  async volver(): Promise<void> {
    if (this.emergenciaId) {
      await this.router.navigate([
        '/alerta-activa',
        this.emergenciaId
      ]);
      return;
    }

    await this.router.navigateByUrl('/emergencias');
  }

  private async mostrarToast(
    mensaje: string,
    color: 'danger' | 'warning' | 'success' | 'primary' = 'primary'
  ): Promise<void> {
    const toast = await this.toastController.create({
      message: mensaje,
      duration: 2600,
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