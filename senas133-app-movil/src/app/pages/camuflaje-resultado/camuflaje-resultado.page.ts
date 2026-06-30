import { Component, OnDestroy } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
import { Subscription } from 'rxjs';
import { CamuflajeResultadoResponse } from '../../core/models/camuflaje.model';
import { CamuflajeService } from '../../core/services/camuflaje.service';

@Component({
  standalone: true,
  selector: 'app-camuflaje-resultado',
  imports: [IonContent, NgIf, NgFor],
  templateUrl: './camuflaje-resultado.page.html',
  styleUrl: './camuflaje-resultado.page.scss'
})
export class CamuflajeResultadoPage implements OnDestroy {
  emergenciaId!: number;
  resultado?: CamuflajeResultadoResponse;
  private sub?: Subscription;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly camuflaje: CamuflajeService
  ) {}

  ionViewWillEnter(): void {
    this.emergenciaId = Number(this.route.snapshot.paramMap.get('emergenciaId'));
    this.sub = this.camuflaje.observarResultado(this.emergenciaId).subscribe({
      next: result => this.resultado = result
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  siguiente(): void {
    this.router.navigateByUrl('/inicio');
  }

  get confirmado(): boolean {
    return !!this.resultado?.confirmadaPorCenco;
  }
}
