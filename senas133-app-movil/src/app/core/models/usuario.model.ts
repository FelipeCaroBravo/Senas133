export interface Usuario {
  id: number;
  rut?: string;
  nombreCompleto?: string;
  direccionPrincipal?: string;
  telefono?: string;
  claveUnicaValidada?: boolean;
  perfilCompleto?: boolean;
}

export interface ClaveUnicaMockResponse {
  usuarioId: number;
  rut: string;
  nombreCompleto: string;
  claveUnicaValidada: boolean;
  perfilCompleto: boolean;
}

export interface CompletarPerfilRequest {
  direccionPrincipal: string;
  telefono: string;
  pin: string;
}
