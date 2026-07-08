import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { addIcons } from 'ionicons';
import { homeOutline, phonePortraitOutline } from 'ionicons/icons';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {
  IonContent,
  IonInput,
  IonButton,
  ToastController,
  IonIcon
} from '@ionic/angular/standalone';

import { AuthService } from '../../core/services/auth.service';
import { StorageService } from '../../core/services/storage.service';

@Component({
  standalone: true,
  selector: 'app-completar-perfil',
 imports: [
    CommonModule,
    FormsModule,
    IonButton,
    IonContent,
    IonInput,
    IonIcon
  ],
  templateUrl: './completar-perfil.page.html',
  styleUrl: './completar-perfil.page.scss'
})
export class CompletarPerfilPage {
  direccionPrincipal = '';
  telefono = '+56 9 ';
  pin = '';
  guardando = false;

  constructor(
    private readonly router: Router,
    private readonly authService: AuthService,
    private readonly storage: StorageService,
    private readonly toastController: ToastController
  ) {
    addIcons({
      homeOutline,
      phonePortraitOutline
    });
  }

  validarPin(event: any): void {
    const valor = event.detail?.value ?? event.target?.value ?? '';

    this.pin = String(valor)
      .replace(/\D/g, '')
      .slice(0, 4);
  }

  async finalizar(): Promise<void> {
    if (this.guardando) {
      return;
    }

    const usuario = await this.storage.getUsuario();

    if (!usuario?.id) {
      await this.mostrarToast(
        'Primero debes validar identidad con Clave Única.',
        'warning'
      );

      await this.router.navigateByUrl('/inicio');
      return;
    }

    if (
      !this.direccionPrincipal.trim() ||
      !this.telefono.trim() ||
      this.pin.length !== 4
    ) {
      await this.mostrarToast(
        'Completa dirección, teléfono y PIN de 4 dígitos.',
        'warning'
      );
      return;
    }

    this.guardando = true;

    this.authService.completarPerfil(usuario.id, {
      direccionPrincipal: this.direccionPrincipal,
      telefono: this.telefono,
      pin: this.pin
    }).subscribe({
      next: async () => {
        this.guardando = false;

        await this.mostrarToast(
          'Perfil completado correctamente.',
          'success'
        );

        await this.router.navigateByUrl('/pin');
      },
      error: async (error) => {
        console.error(error);
        this.guardando = false;

        await this.mostrarToast(
          'No se pudo completar el perfil. Revisa que el backend esté disponible.',
          'danger'
        );
      }
    });
  }

  volver(): void {
    if (this.guardando) {
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