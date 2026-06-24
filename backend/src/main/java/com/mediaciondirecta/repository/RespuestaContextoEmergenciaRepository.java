package com.mediaciondirecta.repository;

import com.mediaciondirecta.entity.RespuestaContextoEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaContextoEmergenciaRepository extends JpaRepository<RespuestaContextoEmergencia, Long> {
    List<RespuestaContextoEmergencia> findByEmergenciaIdOrderByOrdenAsc(Long emergenciaId);
}
