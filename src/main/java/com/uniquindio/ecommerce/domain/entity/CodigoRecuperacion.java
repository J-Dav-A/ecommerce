package com.uniquindio.ecommerce.domain.entity;

import java.time.LocalDateTime;

// Código de un solo uso para recuperar la contraseña. Se guarda cifrado (hash), vale 15 minutos y
// se invalida tras 5 intentos fallidos. Hay a lo sumo uno por usuario: pedir otro reemplaza el anterior.
public class CodigoRecuperacion {

    public static final int MINUTOS_DE_VALIDEZ = 15;
    public static final int MAX_INTENTOS = 5;

    private final String usuarioId;
    private final String codigoHash;
    private final LocalDateTime expiraEn;
    private int intentos;

    private CodigoRecuperacion(String usuarioId, String codigoHash, LocalDateTime expiraEn, int intentos) {
        this.usuarioId = usuarioId;
        this.codigoHash = codigoHash;
        this.expiraEn = expiraEn;
        this.intentos = intentos;
    }

    public static CodigoRecuperacion crear(String usuarioId, String codigoHash, LocalDateTime ahora) {
        return new CodigoRecuperacion(usuarioId, codigoHash, ahora.plusMinutes(MINUTOS_DE_VALIDEZ), 0);
    }

    public static CodigoRecuperacion reconstruir(String usuarioId, String codigoHash, LocalDateTime expiraEn, int intentos) {
        return new CodigoRecuperacion(usuarioId, codigoHash, expiraEn, intentos);
    }

    public boolean estaVigente(LocalDateTime ahora) {
        return ahora.isBefore(expiraEn) && intentos < MAX_INTENTOS;
    }

    public void registrarIntentoFallido() { this.intentos++; }

    public String getUsuarioId() { return usuarioId; }
    public String getCodigoHash() { return codigoHash; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public int getIntentos() { return intentos; }
}