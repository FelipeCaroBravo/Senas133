import { Injectable } from '@angular/core';
import { Preferences } from '@capacitor/preferences';
import { Usuario } from '../models/usuario.model';

const USER_KEY = 'senas133_usuario';

@Injectable({ providedIn: 'root' })
export class StorageService {
  async setUsuario(usuario: Usuario): Promise<void> {
    await Preferences.set({ key: USER_KEY, value: JSON.stringify(usuario) });
  }

  async getUsuario(): Promise<Usuario | null> {
    const result = await Preferences.get({ key: USER_KEY });
    if (!result.value) return null;
    try {
      return JSON.parse(result.value) as Usuario;
    } catch {
      return null;
    }
  }

  async clearUsuario(): Promise<void> {
    await Preferences.remove({ key: USER_KEY });
  }
}
