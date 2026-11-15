package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.FirmaSubidaResponse;
import com.uniquindio.ecommerce.application.usecase.GenerarFirmaDeSubida;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archivos")
@RequiredArgsConstructor
public class ArchivoController {

    private final GenerarFirmaDeSubida generarFirmaDeSubida;

    // Cualquier usuario autenticado (el comprador sube su foto de perfil; el vendedor, además, las imágenes de ofertas)
    @GetMapping("/firma")
    public FirmaSubidaResponse firma(@RequestParam(name = "carpeta", defaultValue = "ofertas") String carpeta) {
        return generarFirmaDeSubida.ejecutar(carpeta, System.currentTimeMillis() / 1000);
    }
}