export type TipoEmergencia = 'DELITOS_GRAVES' | 'ASISTENCIA_VIAL' | 'SOSPECHA_PREVENCION';

export type EstadoEmergencia =
  | 'ENVIADA'
  | 'RECIBIDA'
  | 'EN_ATENCION'
  | 'PATRULLA_DESPACHADA'
  | 'INTERPRETE_SOLICITADO'
  | 'INTERPRETE_CONECTADO'
  | 'CANCELADA'
  | 'CERRADA';

export interface Emergencia {
  id: number;
  usuarioId?: number;
  tipo: TipoEmergencia;
  subtipo?: string | null;
  mensaje?: string | null;
  estado: EstadoEmergencia;
  latitud?: number;
  longitud?: number;
  precisionMetros?: number;
  modoCamuflaje?: boolean;
  requiereInterprete?: boolean;
  canal?: 'INTERNET' | 'SMS';
  creadoEn?: string;
  actualizadoEn?: string;
}

export interface CrearEmergenciaRequest {
  usuarioId: number;
  tipo: TipoEmergencia;
  subtipo?: string | null;
  mensaje?: string | null;
  fraseCodigo?: string | null;
  modoCamuflaje?: boolean;
  requiereInterprete?: boolean;
  canal?: 'INTERNET' | 'SMS';
  latitud: number;
  longitud: number;
  precisionMetros: number;
}

export interface DetalleEmergenciaRequest {
  subtipo: string;
  mensaje?: string;
}

export interface UbicacionRequest {
  latitud: number;
  longitud: number;
  precisionMetros: number;
  fuente: 'GPS' | 'SIMULADA' | 'RED';
}

export interface OpcionEmergencia {
  tipo: TipoEmergencia;
  subtipo: string;
  titulo: string;
  descripcion: string;
  icono?: string;
  iconoImg?: string;
}
