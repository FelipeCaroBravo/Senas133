import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';
import { Emergencia } from '../../core/models/emergencia.model';
import { EmergenciaService } from '../../core/services/emergencia.service';

@Component({
  standalone: true,
  selector: 'app-pregunta-info',
  imports: [IonContent],
  templateUrl: './pregunta-info.page.html',
  styleUrl: './pregunta-info.page.scss'
})
export class PreguntaInfoPage implements OnDestroy {
  emergenciaId!: number;
  emergencia?: Emergencia;
  recibida = false;
  private sub?: Subscription;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly emergenciaService: EmergenciaService
  ) {}

  ionViewWillEnter(): void {
    this.emergenciaId = Number(this.route.snapshot.paramMap.get('emergenciaId'));
    this.sub = this.emergenciaService.observar(this.emergenciaId).subscribe({
      next: emergencia => {
        this.emergencia = emergencia;
        this.recibida = ['RECIBIDA', 'EN_ATENCION', 'PATRULLA_DESPACHADA'].includes(emergencia.estado);
      }
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  si(): void {
    this.router.navigate(['/detalle', 'DELITOS_GRAVES'], { queryParams: { emergenciaId: this.emergenciaId } });
  }

  no(): void {
    this.router.navigate(['/alerta-activa', this.emergenciaId]);
  }

  volver(): void {
    this.router.navigate(['/alerta-activa', this.emergenciaId]);
  }
}
