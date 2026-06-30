import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
import { PreguntaCamuflaje, RespuestaCamuflajeRequest } from '../../core/models/camuflaje.model';
import { CamuflajeService } from '../../core/services/camuflaje.service';
import { LocationService } from '../../core/services/location.service';
import { StorageService } from '../../core/services/storage.service';

@Component({
  standalone: true,
  selector: 'app-camuflaje',
  imports: [IonContent, NgFor, NgIf],
  templateUrl: './camuflaje.page.html',
  styleUrl: './camuflaje.page.scss'
})
export class CamuflajePage {
  piezaColocada = false;
  preguntas: PreguntaCamuflaje[] = [
    { codigo: 'AGRESOR_CERCA', textoCamuflado: '¿Hay un enemigo cerca?' },
    { codigo: 'HAY_ARMA', textoCamuflado: '¿El enemigo tiene objeto peligroso?' },
    { codigo: 'NECESITA_AYUDA_INMEDIATA', textoCamuflado: '¿Necesitas pasar al siguiente nivel ahora?' }
  ];
  respuestas: RespuestaCamuflajeRequest[] = [];
  indice = 0;
  enviando = false;

  constructor(
    private readonly router: Router,
    private readonly camuflajeService: CamuflajeService,
    private readonly location: LocationService,
    private readonly storage: StorageService
  ) {}

  ionViewWillEnter(): void {
    this.camuflajeService.configuracion().subscribe({
      next: config => {
        if (config.preguntas?.length) this.preguntas = config.preguntas;
      }
    });
  }

  permitirSoltar(event: DragEvent): void {
    event.preventDefault();
  }

  colocarPieza(): void {
    this.piezaColocada = true;
  }

  responder(respuesta: boolean): void {
    const pregunta = this.preguntas[this.indice];
    this.respuestas.push({ codigoPregunta: pregunta.codigo, respuesta });

    if (this.indice < this.preguntas.length - 1) {
      this.indice++;
      return;
    }

    this.enviarAlertaCamuflada();
  }

  async enviarAlertaCamuflada(): Promise<void> {
    if (this.enviando) return;
    this.enviando = true;

    const usuario = await this.storage.getUsuario();
    if (!usuario?.id) {
      alert('Debes iniciar sesión antes de usar el modo camuflaje.');
      await this.router.navigateByUrl('/inicio');
      return;
    }

    const ubicacion = await this.location.getCurrentLocation();
    this.camuflajeService.crearEmergencia({
      usuarioId: usuario.id,
      latitud: ubicacion.latitud,
      longitud: ubicacion.longitud,
      precisionMetros: ubicacion.precisionMetros,
      respuestas: this.respuestas
    }).subscribe({
      next: response => this.router.navigate(['/camuflaje-resultado', response.emergenciaId]),
      error: () => {
        this.enviando = false;
        alert('No se pudo enviar la alerta camuflada.');
      }
    });
  }

  volver(): void {
    this.router.navigateByUrl('/inicio');
  }
}
