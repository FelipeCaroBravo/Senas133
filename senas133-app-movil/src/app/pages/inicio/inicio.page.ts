import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {
  IonContent,
  ToastController
} from '@ionic/angular/standalone';

import { AuthService } from '../../core/services/auth.service';
import { StorageService } from '../../core/services/storage.service';

@Component({
  standalone: true,
  selector: 'app-inicio',
  imports: [IonContent],
  templateUrl: './inicio.page.html',
  styleUrl: './inicio.page.scss'
})
export class InicioPage {
  cargandoClaveUnica = false;

  constructor(
    private readonly router: Router,
    private readonly authService: AuthService,
    private readonly storage: StorageService,
    private readonly toastController: ToastController
  ) {}

  async ionViewWillEnter(): Promise<void> {
    const usuario = await this.storage.getUsuario();

    if (usuario?.perfilCompleto) {
      // El diseño mantiene la pantalla inicial, pero el usuario puede entrar directo con PIN.
    }
  }

  ingresarPin(): void {
    this.router.navigateByUrl('/pin');
  }

  camuflaje(): void {
    this.router.navigateByUrl('/camuflaje');
  }

  claveUnica(): void {
    if (this.cargandoClaveUnica) {
      return;
    }

    this.cargandoClaveUnica = true;

    this.authService.claveUnicaMock().subscribe({
      next: async () => {
        this.cargandoClaveUnica = false;
        await this.router.navigateByUrl('/completar-perfil');
      },
      error: async (error) => {
        console.error(error);
        this.cargandoClaveUnica = false;

        await this.mostrarToast(
          'No se pudo simular Clave Única. Revisa que el backend esté activo.',
          'danger'
        );
      }
    });
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
