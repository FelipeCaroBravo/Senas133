package com.mediaciondirecta.repository;

import com.mediaciondirecta.entity.UbicacionEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UbicacionEmergenciaRepository extends JpaRepository<UbicacionEmergencia, Long> {
    List<UbicacionEmergencia> findByEmergenciaIdOrderByCreadoEnDesc(Long emergenciaId);
    Optional<UbicacionEmergencia> findTopByEmergenciaIdOrderByCreadoEnDesc(Long emergenciaId);
}
