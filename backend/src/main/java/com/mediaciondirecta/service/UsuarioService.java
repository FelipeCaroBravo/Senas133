package com.mediaciondirecta.service;

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

        // En prototipo se marca como verificado. En una app real habría validación documental/externa.
        usuario.setVerificado(true);

        Usuario guardado = usuarioRepository.save(usuario);
        return UsuarioResponse.from(guardado);
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
