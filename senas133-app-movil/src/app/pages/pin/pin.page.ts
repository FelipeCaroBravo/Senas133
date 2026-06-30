import { Component } from '@angular/core';
import { NgFor } from '@angular/common';
import { Router } from '@angular/router';
import {
  IonContent,
  ToastController
} from '@ionic/angular/standalone';

import { AuthService } from '../../core/services/auth.service';
import { StorageService } from '../../core/services/storage.service';

@Component({
  standalone: true,
  selector: 'app-pin',
  imports: [IonContent, NgFor],
  templateUrl: './pin.page.html',
  styleUrl: './pin.page.scss'
})
export class PinPage {
  pin = '';
  numeros = ['1', '2', '3', '4', '5', '6', '7', '8', '9'];
  validando = false;

  constructor(
    private readonly router: Router,
    private readonly storage: StorageService,
    private readonly authService: AuthService,
    private readonly toastController: ToastController
  ) {}

  add(value: string): void {
    if (this.validando || this.pin.length >= 4) {
      return;
    }

    this.pin += value;

    if (this.pin.length === 4) {
      this.login();
    }
  }

  borrar(): void {
    if (this.validando) {
      return;
    }

    this.pin = this.pin.slice(0, -1);
  }

  async login(): Promise<void> {
    const usuario = await this.storage.getUsuario();

    if (!usuario?.id) {
      this.pin = '';

      await this.mostrarToast(
        'No hay usuario guardado. Ingresa con Clave Única primero.',
        'warning'
      );

      await this.router.navigateByUrl('/inicio');
      return;
    }

    this.validando = true;

    this.authService.loginPin(usuario.id, this.pin).subscribe({
      next: async () => {
        this.validando = false;
        await this.router.navigateByUrl('/emergencias');
      },
      error: async () => {
        this.pin = '';
        this.validando = false;

        await this.mostrarToast(
          'PIN incorrecto o backend no disponible.',
          'danger'
        );
      }
    });
  }

  volver(): void {
    if (this.validando) {
      return;
    }

    this.router.navigateByUrl('/inicio');
  }

  recuperar(): void {
    if (this.validando) {
      return;
    }

    this.router.navigateByUrl('/completar-perfil');
  }

  private async mostrarToast(
    mensaje: string,
    color: 'danger' | 'warning' | 'success' | 'primary' = 'primary'
  ): Promise<void> {
    const toast = await this.toastController.create({
      message: mensaje,
      duration: 2500,
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