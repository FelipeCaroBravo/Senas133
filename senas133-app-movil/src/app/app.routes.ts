import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'inicio', pathMatch: 'full' },
  {
    path: 'inicio',
    loadComponent: () => import('./pages/inicio/inicio.page').then(m => m.InicioPage)
  },
  {
    path: 'completar-perfil',
    loadComponent: () => import('./pages/completar-perfil/completar-perfil.page').then(m => m.CompletarPerfilPage)
  },
  {
    path: 'pin',
    loadComponent: () => import('./pages/pin/pin.page').then(m => m.PinPage)
  },
  {
    path: 'emergencias',
    loadComponent: () => import('./pages/emergencias/emergencias.page').then(m => m.EmergenciasPage)
  },

  {
    path: 'sms-emergencia',
    loadComponent: () => import('./pages/sms-emergencia/sms-emergencia.page').then(m => m.SmsEmergenciaPage)
  },
  
  {
    path: 'perfil',
    loadComponent: () => import('./pages/perfil/perfil.page').then(m => m.PerfilPage)
  },
  {
    path: 'detalle/:tipo',
    loadComponent: () => import('./pages/detalle-categoria/detalle-categoria.page').then(m => m.DetalleCategoriaPage)
  },
  {
    path: 'pregunta-info/:emergenciaId',
    loadComponent: () => import('./pages/pregunta-info/pregunta-info.page').then(m => m.PreguntaInfoPage)
  },
  {
    path: 'alerta-activa/:emergenciaId',
    loadComponent: () => import('./pages/alerta-activa/alerta-activa.page').then(m => m.AlertaActivaPage)
  },
  {
    path: 'interprete/:emergenciaId',
    loadComponent: () => import('./pages/interprete-call/interprete-call.page').then(m => m.InterpreteCallPage)
  },
  {
    path: 'camuflaje',
    loadComponent: () => import('./pages/camuflaje/camuflaje.page').then(m => m.CamuflajePage)
  },
  {
    path: 'camuflaje-resultado/:emergenciaId',
    loadComponent: () => import('./pages/camuflaje-resultado/camuflaje-resultado.page').then(m => m.CamuflajeResultadoPage)
  },
  { path: '**', redirectTo: 'inicio' }
];
