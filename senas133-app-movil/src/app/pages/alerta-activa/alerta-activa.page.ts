import { Component, OnDestroy } from '@angular/core';
import { NgIf, NgClass } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';
import { Emergencia } from '../../core/models/emergencia.model';
import { EmergenciaService } from '../../core/services/emergencia.service';

@Component({
  standalone: true,
  selector: 'app-alerta-activa',
  imports: [IonContent, NgIf, NgClass],
  templateUrl: './alerta-activa.page.html',
  styleUrl: './alerta-activa.page.scss'
})
export class AlertaActivaPage implements OnDestroy {
  emergenciaId!: number;
  emergencia?: Emergencia;
  private sub?: Subscription;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly emergenciaService: EmergenciaService
  ) {}

  ionViewWillEnter(): void {
    this.emergenciaId = Number(this.route.snapshot.paramMap.get('emergenciaId'));
    this.sub = this.emergenciaService.observar(this.emergenciaId).subscribe({
      next: emergencia => this.emergencia = emergencia
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  get claseColor(): string {
    if (this.emergencia?.tipo === 'ASISTENCIA_VIAL') return 'orange';
    if (this.emergencia?.tipo === 'SOSPECHA_PREVENCION') return 'blue';
    return 'red';
  }

  get subtitulo(): string {
    const raw = this.emergencia?.subtipo || this.emergencia?.mensaje || 'Alerta Silenciosa';
    return raw.toLowerCase().split('_').map(p => p.charAt(0).toUpperCase() + p.slice(1)).join(' ');
  }

  get centralNotificada(): boolean {
    return !!this.emergencia && ['RECIBIDA', 'EN_ATENCION', 'PATRULLA_DESPACHADA', 'INTERPRETE_SOLICITADO', 'INTERPRETE_CONECTADO'].includes(this.emergencia.estado);
  }

  get patrulla(): boolean {
    return !!this.emergencia && ['PATRULLA_DESPACHADA', 'EN_ATENCION', 'INTERPRETE_SOLICITADO', 'INTERPRETE_CONECTADO'].includes(this.emergencia.estado);
  }

  conectarInterprete(): void {
    this.emergenciaService.solicitarInterprete(this.emergenciaId).subscribe({
      next: () => this.router.navigate(['/interprete', this.emergenciaId]),
      error: () => this.router.navigate(['/interprete', this.emergenciaId])
    });
  }

  volver(): void {
    this.router.navigateByUrl('/emergencias');
  }
}
