-- Usuarios de prueba
INSERT INTO usuarios (id, nombre, email, contrasena, telefono, rol, fecha_nacimiento) VALUES
                                                                                          ('usuario-vendedor-1', 'Erwin Vendedor', 'vendedor@uq.edu.co', '123456', '3001234567', 'VENDEDOR', '1998-05-10'),
                                                                                          ('usuario-comprador-1', 'Juan Comprador', 'comprador@uq.edu.co', '123456', '3007654321', 'COMPRADOR', '2000-03-22');

-- Ofertas
INSERT INTO ofertas (id, vendedor_id, titulo, precio_monto, precio_moneda, stock, plataforma, estado) VALUES
                                                                                                          ('oferta-1', 'usuario-vendedor-1', 'The Legend of Zordar', 59.99, 'USD', 5, 'PC', 'PUBLICADA'),
                                                                                                          ('oferta-2', 'usuario-vendedor-1', 'Carrera Infinita 3', 39.99, 'USD', 2, 'PLAYSTATION', 'PUBLICADA'),
                                                                                                          ('oferta-3', 'usuario-vendedor-1', 'Mundo Pixelado', 19.99, 'USD', 0, 'NINTENDO', 'BORRADOR');

-- Claves digitales
INSERT INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado, comprador_id, fecha_asignacion) VALUES
                                                                                                                                 ('clave-1', 'oferta-1', 'ZORDAR-AAAA-1111', 1, false, 'DISPONIBLE', NULL, NULL),
                                                                                                                                 ('clave-2', 'oferta-1', 'ZORDAR-BBBB-2222', 1, false, 'DISPONIBLE', NULL, NULL),
                                                                                                                                 ('clave-3', 'oferta-2', 'CARRERA-CCCC-3333', 1, false, 'ASIGNADA', 'usuario-comprador-1', '2026-10-01T10:00:00Z');

-- Compras
INSERT INTO compras (id, comprador_id, oferta_id, precio_monto, precio_moneda, estado, fecha_compra) VALUES
    ('compra-1', 'usuario-comprador-1', 'oferta-2', 39.99, 'USD', 'COMPLETADA', '2026-10-01T10:00:00');

-- Comentarios
INSERT INTO comentarios (id, comprador_id, oferta_id, texto) VALUES
    ('comentario-1', 'usuario-comprador-1', 'oferta-2', 'Excelente juego, muy recomendado');