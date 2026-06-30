export type TipoEmergencia = 'DELITOS_GRAVES' | 'ASISTENCIA_VIAL' | 'SOSPECHA_PREVENCION';

export type SubtipoIncidente =
  | 'ROBO_CON_VIOLENCIA'
  | 'ASALTO_EN_CURSO'
  | 'AGRESION_FISICA'
  | 'ACCIDENTE_TRANSITO'
  | 'PERSONA_SOSPECHOSA'
  | 'PELEAS_DESORDENES'
  | 'VEHICULO_SOSPECHOSO'
  | 'PERSONA_MERODEANDO'
  | 'MARCAJE_CASA';

export type EstadoEmergencia =
  | 'ACTIVA'
  | 'ENVIADA'
  | 'RECIBIDA'
  | 'EN_ATENCION'
  | 'PATRULLA_DESPACHADA'
  | 'INTERPRETE_SOLICITADO'
  | 'INTERPRETE_CONECTADO'
  | 'CANCELADA'
  | 'CERRADA';

export type CanalAlerta = 'INTERNET' | 'SMS_CONTINGENCIA' | 'CAMUFLAJE';

export interface Emergencia {
  id: number;
  usuarioId: number;
  nombreUsuario: string;
  telefonoUsuario: string;
  direccionPrincipalUsuario: string;
  tipo: TipoEmergencia;
  subtipo: SubtipoIncidente | null;
  estado: EstadoEmergencia;
  canal: CanalAlerta;
  mensaje: string | null;
  fraseCodigo: string | null;
  modoCamuflaje: boolean;
  requiereInterprete: boolean;
  detallePosteriorPendiente: boolean;
  lugarSeguroParaMasInformacion: boolean | null;
  codigoExterno: string | null;
  latitud: number | null;
  longitud: number | null;
  precisionMetros: number | null;
  creadoEn: string;
  actualizadoEn: string;
  recibidaEn: string | null;
  patrullaDespachadaEn: string | null;
}

export interface UbicacionEmergencia {
  id: number;
  emergenciaId: number;
  latitud: number;
  longitud: number;
  precisionMetros: number | null;
  fuente: string | null;
  creadoEn: string;
}

export interface RespuestaContexto {
  id: number;
  emergenciaId: number;
  codigoPregunta: string;
  textoPregunta: string;
  respuesta: boolean;
  creadoEn: string;
}

export type EstadoSolicitudInterprete = 'PENDIENTE' | 'ASIGNADO' | 'EN_LLAMADA' | 'FINALIZADO';

export interface SolicitudInterprete {
  id: number;
  emergenciaId: number;
  estado: EstadoSolicitudInterprete;
  nombreInterprete: string | null;
  urlSalaVideo: string | null;
  creadoEn: string;
  actualizadoEn: string;
}

export interface EstadisticasDashboard {
  totalActivas: number;
  sinConfirmar: number;
  patrullasDespachadas: number;
  camufladas: number;
}
