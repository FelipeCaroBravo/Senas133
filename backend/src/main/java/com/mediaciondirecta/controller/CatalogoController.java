package com.mediaciondirecta.controller;

import com.mediaciondirecta.dto.CatalogoResponse;
import com.mediaciondirecta.service.CatalogoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/emergencias")
    public CatalogoResponse obtenerCatalogoEmergencias() {
        return catalogoService.obtenerCatalogo();
    }
}
