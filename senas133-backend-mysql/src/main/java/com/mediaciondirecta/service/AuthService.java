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

    public UsuarioResponse loginConPinUsuario(LoginPinRequest request) {
        Usuario usuario = buscarUsuarioParaLogin(request);
        validarPin(usuario, request.pin());
        // Para el prototipo móvil se devuelve el usuario directo, porque la app guarda el acceso localmente.
        // También se crea sesión en memoria para mantener compatibilidad con endpoints protegidos si se usa token.
        crearSesionParaUsuario(usuario);
        return UsuarioResponse.from(usuario);
    }

    public LoginResponse loginConPinToken(LoginPinRequest request) {
        Usuario usuario = buscarUsuarioParaLogin(request);
        validarPin(usuario, request.pin());
        return crearSesionParaUsuario(usuario);
    }

    private Usuario buscarUsuarioParaLogin(LoginPinRequest request) {
        if (request.usuarioId() != null) {
            return usuarioRepository.findById(request.usuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        }

        if (request.rut() == null || request.rut().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar usuarioId o rut");
        }

        String rutNormalizado = usuarioService.normalizarRut(request.rut());
        return usuarioRepository.findByRut(rutNormalizado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
    }

    private void validarPin(Usuario usuario, String pin) {
        if (!usuario.isPerfilCompleto() || usuario.getPinHash() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El perfil aún no tiene PIN configurado");
        }

        if (!pinService.matches(pin, usuario.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
    }

    public LoginResponse crearSesionParaUsuario(Usuario usuario) {
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

    public Long resolverUsuarioId(String authorizationHeader, Long usuarioIdFallback) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return requerirUsuarioId(authorizationHeader);
        }
        if (usuarioIdFallback != null) {
            return usuarioIdFallback;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debe enviar Authorization o usuarioId");
    }

    private String extraerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debe enviar Authorization: Bearer <token>");
        }
        return authorizationHeader.substring("Bearer ".length()).trim();
    }
}
