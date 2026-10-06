# Diagrama de agregado - Marketplace de videojuegos

Grupo: Erwin Harder Garzón y Juan David Arango Valencia

## Agregado 1: Oferta

Raíz: Oferta
Dentro del límite: Plataforma, Precio, EstadoOferta
Fuera del límite (por id): Vendedor, Contenido (Videojuego / DLC / Expansión), Compra

Invariantes:
1. Una Oferta nunca puede publicarse sin una Plataforma definida.
2. El stock nunca puede ser negativo.
3. Una Oferta con compras asociadas nunca se elimina físicamente, solo de forma lógica.
4. El precio siempre debe ser mayor que cero.
5. Una Oferta eliminada lógicamente nunca puede volver a publicarse.

## Agregado 2: Clave digital

Raíz: ClaveDigital
Dentro del límite: EstadoClave, LicenciaDigital
Fuera del límite (por id): Oferta, Comprador, Compra

Invariantes:
1. Una Clave nunca puede asignarse a más de un comprador.
2. Una Clave asignada, canjeada o reembolsada nunca puede volver a estar disponible.
3. Una Clave canjeada nunca puede pasar a reembolsada.
4. El reembolso siempre exige que la clave no esté canjeada y que se pida dentro del plazo.
5. El código de la Clave siempre debe ser no nulo y único.

## Agregado 3: Compra

Raíz: Compra
Dentro del límite: EstadoCompra, Precio (congelado)
Fuera del límite (por id): Oferta, Comprador

Invariantes:
1. Toda Compra nace siempre en estado PENDIENTE.
2. Una Compra nunca puede crearse sin una Oferta y un Comprador asociados.
3. El precio de una Compra siempre queda congelado al momento de crearse, y nunca cambia aunque el precio de la Oferta cambie después.
4. Una Compra solo puede avanzar de estado siguiendo su ciclo de vida: PENDIENTE -> COMPLETADA -> REEMBOLSADA.
5. Una Compra en estado REEMBOLSADA nunca puede volver a modificarse.
6. Solo una Compra COMPLETADA puede pasar a REEMBOLSADA, y siempre exige un motivo.