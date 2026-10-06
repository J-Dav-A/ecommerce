## Casos de uso identificados

| # | Caso de uso | Quién lo usa | Qué hace | Repository(s) que necesita |
|---|---|---|---|---|
| 1 | PublicarProductoUseCase | Vendedor | Crea una Oferta, le define la plataforma y la publica en el catálogo | OfertaRepository |
| 2 | BuscarOfertas | Comprador | Consulta las ofertas publicadas en el catálogo | OfertaRepository |
| 3 | AsignarClaveDigital | Vendedor / sistema | Asigna una clave digital disponible a un comprador | ClaveDigitalRepository |
| 4 | RegistrarCompra | Comprador | Registra la compra de una oferta publicada, descuenta el stock y congela el precio | OfertaRepository, CompraRepository |
| 5 | SolicitarReembolso | Comprador | Solicita el reembolso de una clave digital, si no fue canjeada y está dentro del plazo | ClaveDigitalRepository |
| 6 | DejarComentario | Comprador | Deja un comentario y calificación sobre un producto, solo si tiene una compra completada | CompraRepository, ComentarioRepository |
| 7 | ConsultarHistorialDeCompras | Comprador | Consulta todas las compras realizadas por un comprador | CompraRepository |
| 8 | RegistrarUsuario | Comprador / Vendedor | Registra un nuevo usuario en la plataforma, validando que el email sea único | UsuarioRepository |