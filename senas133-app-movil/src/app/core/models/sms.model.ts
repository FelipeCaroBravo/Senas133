export interface SmsPreparado {
  numeroDestino: string;
  textoSms: string;
  largoTexto: number;
  requiereConfirmacionUsuario: boolean;
  instruccion: string;
}
