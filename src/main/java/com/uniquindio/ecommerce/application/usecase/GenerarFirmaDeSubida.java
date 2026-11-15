package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.dto.response.FirmaSubidaResponse;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Set;

// Las imágenes (y la foto de perfil) se suben desde el cliente directo a Cloudinary con una firma que
// genera el servidor; el secreto de la API nunca sale del backend. Luego el cliente guarda la URL resultante.
@Service
public class GenerarFirmaDeSubida {

    private static final Set<String> CARPETAS_PERMITIDAS = Set.of("ofertas", "perfiles");

    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public GenerarFirmaDeSubida(@Value("${cloudinary.cloud-name:}") String cloudName,
                                @Value("${cloudinary.api-key:}") String apiKey,
                                @Value("${cloudinary.api-secret:}") String apiSecret) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public FirmaSubidaResponse ejecutar(String carpeta, long timestamp) {
        if (carpeta == null || !CARPETAS_PERMITIDAS.contains(carpeta)) {
            throw new ReglaDominioException("La carpeta debe ser una de: " + String.join(", ", CARPETAS_PERMITIDAS));
        }
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new ServicioNoDisponibleException("La subida de imágenes no está configurada");
        }
        String carpetaCompleta = "ecommerce/" + carpeta;
        // Cloudinary firma los parámetros ordenados alfabéticamente, más el secreto, con SHA-1
        String firma = sha1("folder=" + carpetaCompleta + "&timestamp=" + timestamp + apiSecret);
        return new FirmaSubidaResponse(cloudName, apiKey, timestamp, carpetaCompleta, firma,
                "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload");
    }

    private String sha1(String texto) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-1").digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}