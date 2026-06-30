import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';
import { Emergencia } from '../../core/models/emergencia.model';
import { EmergenciaService } from '../../core/services/emergencia.service';

@Component({
  standalone: true,
  selector: 'app-interprete-call',
  imports: [IonContent],
  templateUrl: './interprete-call.page.html',
  styleUrl: './interprete-call.page.scss'
})
export class InterpreteCallPage implements OnDestroy {
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

  get estadoTexto(): string {
    if (this.emergencia?.estado === 'INTERPRETE_CONECTADO') return 'Intérprete conectado';
    return 'Esperando al intérprete...';
  }

  colgar(): void {
    this.router.navigate(['/alerta-activa', this.emergenciaId]);
  }
}
