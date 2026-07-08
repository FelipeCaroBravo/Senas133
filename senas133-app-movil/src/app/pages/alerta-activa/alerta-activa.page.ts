import { Component, OnDestroy } from '@angular/core';
import { NgClass, NgIf } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { IonContent, ToastController } from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';

import { EmergenciaService } from '../../core/services/emergencia.service';
import { Emergencia } from '../../core/models/emergencia.model';

@Component({
  standalone: true,
  selector: 'app-alerta-activa',
  imports: [
    IonContent,
    NgClass,
    NgIf
  ],
  templateUrl: './alerta-activa.page.html',
  styleUrl: './alerta-activa.page.scss'
})
export class AlertaActivaPage implements OnDestroy {
  emergencia?: Emergencia;
  ayudaAbierta = false;
  private subscription?: Subscription;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly emergenciaService: EmergenciaService,
    private readonly toastController: ToastController
  ) {}

  ionViewWillEnter(): void {
    const id = Number(this.route.snapshot.paramMap.get('emergenciaId'));

    if (!id) {
      this.router.navigateByUrl('/emergencias');
      return;
    }

    this.subscription?.unsubscribe();

    this.subscription = this.emergenciaService.observar(id).subscribe({
      next: (emergencia) => {
        this.emergencia = emergencia;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  get claseColor(): string {
    if (this.emergencia?.tipo === 'ASISTENCIA_VIAL') {
      return 'orange';
    }

    if (this.emergencia?.tipo === 'SOSPECHA_PREVENCION') {
      return 'blue';
    }

    return 'red';
  }

  get subtitulo(): string {
    if (this.emergencia?.tipo === 'ASISTENCIA_VIAL') {
      return 'Asistencia vial activada';
    }

    if (this.emergencia?.tipo === 'SOSPECHA_PREVENCION') {
      return 'Reporte preventivo enviado';
    }

    return 'Emergencia enviada a la central';
  }

  get centralNotificada(): boolean {
    return [
      'RECIBIDA',
      'EN_ATENCION',
      'PATRULLA_DESPACHADA',
      'INTERPRETE_SOLICITADO',
      'INTERPRETE_CONECTADO',
      'CERRADA'
    ].includes(this.emergencia?.estado ?? '');
  }

  get patrulla(): boolean {
    return [
      'PATRULLA_DESPACHADA',
      'EN_ATENCION',
      'INTERPRETE_SOLICITADO',
      'INTERPRETE_CONECTADO',
      'CERRADA'
    ].includes(this.emergencia?.estado ?? '');
  }

  conectarInterprete(): void {
    if (!this.emergencia?.id) {
      return;
    }

    this.emergenciaService.solicitarInterprete(this.emergencia.id).subscribe({
      next: async () => {
        await this.router.navigate([
          '/interprete',
          this.emergencia?.id
        ]);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  async cancelarAlerta(): Promise<void> {
    const toast = await this.toastController.create({
      message: 'Alerta cancelada correctamente.',
      duration: 2200,
      position: 'top',
      color: 'success'
    });

    await toast.present();
    await this.router.navigateByUrl('/emergencias');
  }

  volver(): void {
    this.router.navigateByUrl('/emergencias');
  }
}