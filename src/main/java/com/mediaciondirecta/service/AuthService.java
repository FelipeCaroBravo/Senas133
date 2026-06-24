package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.LoginPinRequest;
import com.mediaciondirecta.dto.LoginResponse;
import com.mediaciondirecta.dto.UsuarioResponse;
import com.mediaciondirecta.entity.Usuario;
import com.mediaciondirecta.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PinService pinService;

    // Prototipo: sesión en memoria. En producción reemplazar por JWT/OAuth2/Keycloak.
    private final Map<String, Long> sesiones = new ConcurrentHashMap<>();

    public AuthService(UsuarioRepository usuarioRepository, UsuarioService usuarioService, PinService pinService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.pinService = pinService;
    }

    public LoginResponse loginConPin(LoginPinRequest request) {
        String rutNormalizado = usuarioService.normalizarRut(request.rut());

        Usuario usuario = usuarioRepository.findByRut(rutNormalizado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!pinService.matches(request.pin(), usuario.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = UUID.randomUUID().toString();
        sesiones.put(token, usuario.getId());

        return new LoginResponse(token, UsuarioResponse.from(usuario));
    }

    public void logout(String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        sesiones.remove(token);
    }

    public Long requerirUsuarioId(String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Long usuarioId = sesiones.get(token);

        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o sesión expirada");
        }

        return usuarioId;
    }

    private String extraerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debe enviar Authorization: Bearer <token>");
        }
        return authorizationHeader.substring("Bearer ".length()).trim();
    }
}
