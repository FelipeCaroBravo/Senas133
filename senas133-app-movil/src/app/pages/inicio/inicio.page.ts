import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
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
    private readonly storage: StorageService
  ) {}

  async ionViewWillEnter() {
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
    this.cargandoClaveUnica = true;
    this.authService.claveUnicaMock().subscribe({
      next: () => this.router.navigateByUrl('/completar-perfil'),
      error: () => {
        this.cargandoClaveUnica = false;
        alert('No se pudo simular Clave Única. Revisa que el backend esté activo.');
      }
    });
  }
}
