import { Component } from '@angular/core';
import { NgFor, NgClass, NgIf } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import {
  IonContent,
  ToastController
} from '@ionic/angular/standalone';

import { EmergenciaService } from '../../core/services/emergencia.service';
import { LocationService } from '../../core/services/location.service';
import { StorageService } from '../../core/services/storage.service';
import {
  OpcionEmergencia,
  TipoEmergencia
} from '../../core/models/emergencia.model';

@Component({
  standalone: true,
  selector: 'app-detalle-categoria',
  imports: [IonContent, NgFor, NgClass, NgIf],
  templateUrl: './detalle-categoria.page.html',
  styleUrl: './detalle-categoria.page.scss'
})
export class DetalleCategoriaPage {
  tipo: TipoEmergencia = 'DELITOS_GRAVES';
  emergenciaId?: number;
  opciones: readonly OpcionEmergencia[] = [];
  enviando = false;
  ayudaAbierta = false;

  get sinInternet(): boolean {
    return localStorage.getItem('sinInternetDemo') === 'true' || !navigator.onLine;
  }

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly storage: StorageService,
    private readonly location: LocationService,
    private readonly emergenciaService: EmergenciaService,
    private readonly toastController: ToastController
  ) {}

  ionViewWillEnter(): void {
    this.tipo = this.route.snapshot.paramMap.get('tipo') as TipoEmergencia;

    const id = this.route.snapshot.queryParamMap.get('emergenciaId');
    this.emergenciaId = id ? Number(id) : undefined;

    this.opciones = this.emergenciaService.opcionesPorTipo(this.tipo);
  }

  get titulo(): string {
    if (this.tipo === 'DELITOS_GRAVES') {
      return 'Peligro';
    }

    if (this.tipo === 'ASISTENCIA_VIAL') {
      return 'Apoyo / Calle';
    }

    return 'Veo algo raro';
  }

  get claseColor(): string {
    if (this.tipo === 'DELITOS_GRAVES') {
      return 'red';
    }

    if (this.tipo === 'ASISTENCIA_VIAL') {
      return 'orange';
    }

    return 'blue';
  }

  async seleccionar(opcion: OpcionEmergencia): Promise<void> {
    if (this.enviando) {
      return;
    }

    this.enviando = true;

    if (this.sinInternet) {
      try {
        const ubicacion = await this.location.getCurrentLocation();

        this.enviando = false;

        await this.router.navigate(['/sms-emergencia'], {
          queryParams: {
            tipo: `${this.titulo} - ${opcion.titulo}`,
            mensaje: opcion.descripcion,
            latitud: ubicacion.latitud,
            longitud: ubicacion.longitud
          }
        });

        return;
      } catch (error) {
        console.error(error);
        this.enviando = false;

        await this.mostrarToast(
          'No se pudo obtener la ubicación para preparar el SMS.',
          'danger'
        );

        return;
      }
    }

    if (this.emergenciaId) {
      this.emergenciaService.completarDetalle(this.emergenciaId, {
        subtipo: opcion.subtipo,
        mensaje: opcion.titulo
      }).subscribe({
        next: async (emergencia) => {
          this.enviando = false;
          await this.router.navigate(['/alerta-activa', emergencia.id]);
        },
        error: async (error) => {
          console.error(error);
          this.enviando = false;

          await this.mostrarToast(
            'No se pudo actualizar el detalle.',
            'danger'
          );
        }
      });

      return;
    }

    const usuario = await this.storage.getUsuario();

    if (!usuario?.id) {
      this.enviando = false;

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
        tipo: this.tipo,
        subtipo: opcion.subtipo,
        mensaje: opcion.titulo,
        fraseCodigo: opcion.subtipo,
        modoCamuflaje: false,
        requiereInterprete: true,
        canal: 'INTERNET',
        latitud: ubicacion.latitud,
        longitud: ubicacion.longitud,
        precisionMetros: ubicacion.precisionMetros
      }).subscribe({
        next: async (emergencia) => {
          this.enviando = false;
          await this.router.navigate(['/alerta-activa', emergencia.id]);
        },
        error: async (error) => {
          console.error(error);
          this.enviando = false;

          await this.mostrarToast(
            'No se pudo crear la emergencia.',
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

    this.router.navigateByUrl('/emergencias');
  }

  irCamuflaje(): void {
    if (this.enviando) {
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
      duration: 2800,
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
