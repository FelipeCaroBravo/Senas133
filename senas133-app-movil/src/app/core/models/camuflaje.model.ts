export interface PreguntaCamuflaje {
  codigo: string;
  textoCamuflado: string;
  textoReal?: string;
}

export interface RespuestaCamuflajeRequest {
  codigoPregunta: string;
  respuesta: boolean;
}

export interface CamuflajeEmergenciaRequest {
  usuarioId: number;
  latitud: number;
  longitud: number;
  precisionMetros: number;
  respuestas: RespuestaCamuflajeRequest[];
}

export interface CamuflajeEmergenciaResponse {
  emergenciaId: number;
  estado: string;
  mensajeCamuflado: string;
  puntajeDisponible: boolean;
}

export interface CamuflajeResultadoResponse {
  emergenciaId: number;
  estadoEmergencia: string;
  confirmadaPorCenco: boolean;
  puntaje: number | null;
  mensajePantalla: string;
}
