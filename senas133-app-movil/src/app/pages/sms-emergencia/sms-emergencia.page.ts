import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { IonContent, ToastController } from '@ionic/angular/standalone';

@Component({
  standalone: true,
  selector: 'app-sms-emergencia',
  imports: [IonContent, CommonModule],
  templateUrl: './sms-emergencia.page.html',
  styleUrl: './sms-emergencia.page.scss'
})
export class SmsEmergenciaPage {
  tipo = 'PELIGRO';
  mensaje = 'Necesito ayuda urgente';
  latitud = '';
  longitud = '';
  smsEnviado = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly toastController: ToastController
  ) {}

  ionViewWillEnter(): void {
    this.tipo = this.route.snapshot.queryParamMap.get('tipo') ?? 'PELIGRO';
    this.mensaje = this.route.snapshot.queryParamMap.get('mensaje') ?? 'Necesito ayuda urgente';
    this.latitud = this.route.snapshot.queryParamMap.get('latitud') ?? '';
    this.longitud = this.route.snapshot.queryParamMap.get('longitud') ?? '';
    this.smsEnviado = false;
  }

  get textoSms(): string {
    return `SEÑAS 133: ${this.mensaje}. Tipo: ${this.tipo}. Ubicación: ${this.latitud}, ${this.longitud}`;
  }

  async enviarSms(): Promise<void> {
    const numero = '133';
    const body = encodeURIComponent(this.textoSms);

    // En celular abre la app de mensajes con el SMS preparado.
    window.location.href = `sms:${numero}?body=${body}`;

    // Para la demo web mostramos estado enviado.
    this.smsEnviado = true;

    const toast = await this.toastController.create({
      message: 'SMS preparado con ubicación.',
      duration: 2200,
      position: 'top',
      color: 'success'
    });

    await toast.present();
  }

  volver(): void {
    this.router.navigateByUrl('/emergencias');
  }
}