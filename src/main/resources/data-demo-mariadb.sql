-- Datos de demostración: ofertas extra para ver la paginación y la búsqueda del catálogo.
-- Se cargan SOLO con el perfil "demo" (H2) o con el perfil "mariadb". Las pruebas automáticas NO los usan.

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-103', 'Elden Ring', 'u-1', 39.99, 'USD', 8, 'PUBLICADA', 'PC');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-103', 0, 'https://placehold.co/600x400/1f2a44/ffffff?text=Elden+Ring');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-201', 'o-103', 'ELDEN-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-202', 'o-103', 'ELDEN-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-104', 'Hades II', 'u-1', 29.99, 'USD', 12, 'PUBLICADA', 'PC');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-104', 0, 'https://placehold.co/600x400/5b1f2b/ffffff?text=Hades+II');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-203', 'o-104', 'HADES-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-204', 'o-104', 'HADES-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-105', 'Halo Infinite', 'u-1', 34.99, 'USD', 6, 'PUBLICADA', 'XBOX');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-105', 0, 'https://placehold.co/600x400/1b4d2e/ffffff?text=Halo+Infinite');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-205', 'o-105', 'HALO-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-206', 'o-105', 'HALO-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-106', 'Forza Horizon 5', 'u-1', 44.99, 'USD', 4, 'PUBLICADA', 'XBOX');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-106', 0, 'https://placehold.co/600x400/7a3b00/ffffff?text=Forza+Horizon+5');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-207', 'o-106', 'FORZA-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-208', 'o-106', 'FORZA-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-107', 'Spider-Man 2', 'u-1', 69.99, 'USD', 7, 'PUBLICADA', 'PLAYSTATION');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-107', 0, 'https://placehold.co/600x400/8a1c1c/ffffff?text=Spider-Man+2');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-209', 'o-107', 'SPIDER-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-210', 'o-107', 'SPIDER-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-108', 'The Last of Us Part I', 'u-1', 39.99, 'USD', 5, 'PUBLICADA', 'PLAYSTATION');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-108', 0, 'https://placehold.co/600x400/2e3b2a/ffffff?text=The+Last+of+Us+Part+I');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-211', 'o-108', 'TLOU-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-212', 'o-108', 'TLOU-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-109', 'Super Mario Odyssey', 'u-1', 49.99, 'USD', 9, 'PUBLICADA', 'NINTENDO');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-109', 0, 'https://placehold.co/600x400/b3261e/ffffff?text=Super+Mario+Odyssey');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-213', 'o-109', 'MARIO-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-214', 'o-109', 'MARIO-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-110', 'Hollow Knight', 'u-1', 14.99, 'USD', 20, 'PUBLICADA', 'PC');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-110', 0, 'https://placehold.co/600x400/222b3a/ffffff?text=Hollow+Knight');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-215', 'o-110', 'HOLLOW-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-216', 'o-110', 'HOLLOW-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-111', 'Cyberpunk 2077', 'u-1', 29.99, 'USD', 10, 'PUBLICADA', 'PC');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-111', 0, 'https://placehold.co/600x400/8a7d00/ffffff?text=Cyberpunk+2077');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-217', 'o-111', 'CYBER-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-218', 'o-111', 'CYBER-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-112', 'Mario Kart 8 Deluxe', 'u-1', 54.99, 'USD', 6, 'PUBLICADA', 'NINTENDO');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-112', 0, 'https://placehold.co/600x400/0b5ea8/ffffff?text=Mario+Kart+8+Deluxe');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-219', 'o-112', 'KART-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-220', 'o-112', 'KART-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-113', 'Gears 5', 'u-1', 19.99, 'USD', 3, 'PUBLICADA', 'XBOX');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-113', 0, 'https://placehold.co/600x400/3a3a3a/ffffff?text=Gears+5');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-221', 'o-113', 'GEARS-2026-KEY1', 1, false, 'DISPONIBLE');
INSERT IGNORE INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-222', 'o-113', 'GEARS-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT IGNORE INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-114', 'Hogwarts Legacy', 'u-1', 59.99, 'USD', 0, 'PUBLICADA', 'PLAYSTATION');
INSERT IGNORE INTO oferta_imagenes (oferta_id, orden, url)
VALUES ('o-114', 0, 'https://placehold.co/600x400/3b2a6b/ffffff?text=Hogwarts+Legacy');