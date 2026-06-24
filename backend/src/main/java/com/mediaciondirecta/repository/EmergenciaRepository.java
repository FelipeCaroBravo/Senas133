package com.mediaciondirecta.repository;

import com.mediaciondirecta.entity.Emergencia;
import com.mediaciondirecta.enums.EstadoEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EmergenciaRepository extends JpaRepository<Emergencia, Long> {
    List<Emergencia> findByEstadoInOrderByCreadoEnDesc(Collection<EstadoEmergencia> estados);
    List<Emergencia> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
}
