package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;

public record ClaveResponse(
        String id,
        String ofertaId,
        String codigo,
        String estado,
        int maxActivaciones,
        boolean transferible
) {
    public static ClaveResponse desde(ClaveDigital clave) {
        return new ClaveResponse(
                clave.getId(),
                clave.getOfertaId(),
                clave.getCodigo(),
                clave.getEstado().name(),
                clave.getLicencia().maxActivaciones(),
                clave.getLicencia().transferible()
        );
    }
}