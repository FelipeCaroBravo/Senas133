import { Injectable } from '@angular/core';
import { Geolocation } from '@capacitor/geolocation';
import { UbicacionRequest } from '../models/emergencia.model';

@Injectable({ providedIn: 'root' })
export class LocationService {
  async getCurrentLocation(): Promise<UbicacionRequest> {
    try {
      const position = await Geolocation.getCurrentPosition({
        enableHighAccuracy: true,
        timeout: 5000
      });

      return {
        latitud: position.coords.latitude,
        longitud: position.coords.longitude,
        precisionMetros: position.coords.accuracy ?? 10,
        fuente: 'GPS'
      };
    } catch {
      // Coordenadas simuladas para demo en navegador o cuando el permiso GPS falla.
      return {
        latitud: -36.82699,
        longitud: -73.04977,
        precisionMetros: 8,
        fuente: 'SIMULADA'
      };
    }
  }
}
