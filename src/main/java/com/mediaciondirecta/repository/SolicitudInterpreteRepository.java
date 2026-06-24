package com.mediaciondirecta.repository;

import com.mediaciondirecta.entity.SolicitudInterprete;
import com.mediaciondirecta.enums.EstadoSolicitudInterprete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SolicitudInterpreteRepository extends JpaRepository<SolicitudInterprete, Long> {
    Optional<SolicitudInterprete> findByEmergenciaId(Long emergenciaId);
    List<SolicitudInterprete> findByEstadoInOrderByCreadoEnAsc(Collection<EstadoSolicitudInterprete> estados);
}
