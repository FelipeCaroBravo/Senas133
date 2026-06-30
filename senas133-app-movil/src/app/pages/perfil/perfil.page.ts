import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  IonContent,
  ToastController
} from '@ionic/angular/standalone';

import { AuthService } from '../../core/services/auth.service';
import { StorageService } from '../../core/services/storage.service';

@Component({
  standalone: true,
  selector: 'app-perfil',
  imports: [
    IonContent,
    FormsModule
  ],
  templateUrl: './perfil.page.html',
  styleUrl: './perfil.page.scss'
})
export class PerfilPage {
  usuarioId?: number;
  direccionPrincipal = '';
  telefono = '';
  guardando = false;

  constructor(
    private readonly router: Router,
    private readonly storage: StorageService,
    private readonly auth: AuthService,
    private readonly toastController: ToastController
  ) {}

  async ionViewWillEnter(): Promise<void> {
    const usuario = await this.storage.getUsuario();

    this.usuarioId = usuario?.id;
    this.direccionPrincipal = usuario?.direccionPrincipal ?? '';
    this.telefono = usuario?.telefono ?? '';
  }

  guardar(): void {
    if (this.guardando) {
      return;
    }

    if (!this.usuarioId) {
      this.mostrarToast(
        'No hay usuario guardado. Ingresa nuevamente.',
        'warning'
      );
      return;
    }

    if (!this.direccionPrincipal.trim() || !this.telefono.trim()) {
      this.mostrarToast(
        'Completa dirección y teléfono.',
        'warning'
      );
      return;
    }

    this.guardando = true;

    this.auth.actualizarPerfil(this.usuarioId, {
      direccionPrincipal: this.direccionPrincipal,
      telefono: this.telefono
    }).subscribe({
      next: async (usuarioActualizado) => {
        this.guardando = false;

        const usuarioActual = await this.storage.getUsuario();

        await this.storage.setUsuario({
          ...usuarioActual,
          ...usuarioActualizado,
          direccionPrincipal: this.direccionPrincipal,
          telefono: this.telefono
        });

        await this.mostrarToast(
          'Perfil actualizado correctamente.',
          'success'
        );
      },
      error: async (error) => {
        console.error(error);
        this.guardando = false;

        await this.mostrarToast(
          'No se pudo guardar el perfil. Revisa que el backend esté disponible.',
          'danger'
        );
      }
    });
  }

  volver(): void {
    if (this.guardando) {
      return;
    }

    this.router.navigateByUrl('/emergencias');
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