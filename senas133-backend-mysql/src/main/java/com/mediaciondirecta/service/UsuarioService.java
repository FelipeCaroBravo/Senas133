package com.mediaciondirecta.service;

import com.mediaciondirecta.dto.ActualizarPerfilRequest;
import com.mediaciondirecta.dto.CambiarPinRequest;
import com.mediaciondirecta.dto.ClaveUnicaMockRequest;
import com.mediaciondirecta.dto.CompletarPerfilRequest;
import com.mediaciondirecta.dto.RegistroRequest;
import com.mediaciondirecta.dto.UsuarioResponse;
import com.mediaciondirecta.entity.Usuario;
import com.mediaciondirecta.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PinService pinService;

    public UsuarioService(UsuarioRepository usuarioRepository, PinService pinService) {
        this.usuarioRepository = usuarioRepository;
        this.pinService = pinService;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        String rutNormalizado = normalizarRut(request.rut());

        if (usuarioRepository.existsByRut(rutNormalizado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario registrado con ese RUT");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.nombreCompleto().trim());
        usuario.setRut(rutNormalizado);
        usuario.setNumeroDocumento(request.numeroDocumento().trim());
        usuario.setTelefono(request.telefono().trim());
        usuario.setPinHash(pinService.hash(request.pin()));
        usuario.setVerificado(true);
        usuario.setClaveUnicaValidada(false);
        usuario.setPerfilCompleto(true);

        Usuario guardado = usuarioRepository.save(usuario);
        return UsuarioResponse.from(guardado);
    }

    @Transactional
    public Usuario validarClaveUnicaMock(ClaveUnicaMockRequest request) {
        // Para el prototipo, si el frontend no envía datos, se usa un usuario demo existente.
        String rutEntrada = request.rut() != null && !request.rut().isBlank() ? request.rut() : "11111111-1";
        String rutNormalizado = normalizarRut(rutEntrada);

        Usuario usuario = usuarioRepository.findByRut(rutNormalizado).orElseGet(Usuario::new);
        usuario.setRut(rutNormalizado);
        usuario.setNombreCompleto(request.nombreCompleto() != null && !request.nombreCompleto().isBlank()
                ? request.nombreCompleto().trim()
                : "Usuario Demo Principal");
        usuario.setNumeroDocumento(request.numeroDocumento() != null && !request.numeroDocumento().isBlank()
                ? request.numeroDocumento().trim()
                : "DOC-DEMO-" + rutNormalizado.replace("-", ""));
        usuario.setCodigoDispositivo(request.codigoDispositivo() != null ? request.codigoDispositivo() : "DEVICE-DEMO-MOVIL");
        usuario.setVerificado(true);
        usuario.setClaveUnicaValidada(true);
        // Si ya tenía perfil completo, se conserva. Si es nuevo, queda pendiente de completar.
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioResponse completarPerfil(Long usuarioId, CompletarPerfilRequest request) {
        Usuario usuario = obtenerEntidadPorId(usuarioId);
        if (!usuario.isClaveUnicaValidada()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Debe validar identidad con Clave Única antes de completar perfil");
        }
        usuario.setDireccionPrincipal(request.direccionPrincipal().trim());
        usuario.setTelefono(request.telefono().trim());
        usuario.setPinHash(pinService.hash(request.pin()));
        usuario.setPerfilCompleto(true);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(Long usuarioId, ActualizarPerfilRequest request) {
        Usuario usuario = obtenerEntidadPorId(usuarioId);
        usuario.setDireccionPrincipal(request.direccionPrincipal().trim());
        usuario.setTelefono(request.telefono().trim());
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse cambiarPin(Long usuarioId, CambiarPinRequest request) {
        Usuario usuario = obtenerEntidadPorId(usuarioId);
        if (usuario.getPinHash() == null || !pinService.matches(request.pinActual(), usuario.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "PIN actual inválido");
        }
        usuario.setPinHash(pinService.hash(request.pinNuevo()));
        return UsuarioResponse.from(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerEntidadPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfil(Long id) {
        return UsuarioResponse.from(obtenerEntidadPorId(id));
    }

    public String normalizarRut(String rut) {
        return rut == null ? null : rut.replace(".", "").replace(" ", "").toUpperCase();
    }
}
