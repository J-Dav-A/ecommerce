package com.uniquindio.ecommerce.application.dto.response;

// Datos para que el cliente suba un archivo directo a Cloudinary sin pasar por el servidor
public record FirmaSubidaResponse(String cloudName, String apiKey, long timestamp, String carpeta,
                                  String firma, String urlDeSubida) {}