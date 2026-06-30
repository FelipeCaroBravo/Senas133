package com.mediaciondirecta.repository;

import com.mediaciondirecta.entity.MensajeChatEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeChatEmergenciaRepository extends JpaRepository<MensajeChatEmergencia, Long> {
    List<MensajeChatEmergencia> findByEmergenciaIdOrderByCreadoEnAsc(Long emergenciaId);
}
