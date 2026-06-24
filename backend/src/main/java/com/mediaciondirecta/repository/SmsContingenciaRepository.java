package com.mediaciondirecta.repository;

import com.mediaciondirecta.entity.SmsContingencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmsContingenciaRepository extends JpaRepository<SmsContingencia, Long> {
    List<SmsContingencia> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
}
