-- Usuarios iniciales (Clave encriptada con BCrypt para 'clave123')
INSERT INTO usuarios (id, nombre, email, contrasena, rol, telefono, fecha_nacimiento)
VALUES ('u-1', 'Admin Gamer', 'admin@ecommerce.com', '$2a$10$IPwk7eQeO0qZNU1zSNqxUeFe9maVGKMKjyqvPajRsBV1wxVAF4IFO', 'VENDEDOR', '3001234567', '1995-05-15');

INSERT INTO usuarios (id, nombre, email, contrasena, rol, telefono, fecha_nacimiento)
VALUES ('u-2', 'Juan Perez', 'juan@gmail.com', '$2a$10$IPwk7eQeO0qZNU1zSNqxUeFe9maVGKMKjyqvPajRsBV1wxVAF4IFO', 'COMPRADOR', '3109876543', '2000-10-20');

-- Ofertas de Videojuegos
INSERT INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-101', 'Zelda: Tears of the Kingdom', 'u-1', 59.99, 'USD', 5, 'PUBLICADA', 'NINTENDO');

INSERT INTO ofertas (id, titulo, vendedor_id, precio_monto, precio_moneda, stock, estado, plataforma)
VALUES ('o-102', 'God of War Ragnarok', 'u-1', 49.99, 'USD', 3, 'PUBLICADA', 'PLAYSTATION');

-- Claves Digitales de prueba
INSERT INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-001', 'o-101', 'ZELDA-2026-KEY1', 1, false, 'DISPONIBLE');

INSERT INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-002', 'o-101', 'ZELDA-2026-KEY2', 1, false, 'DISPONIBLE');

INSERT INTO claves_digitales (id, oferta_id, codigo, max_activaciones, transferible, estado)
VALUES ('k-003', 'o-102', 'GOWR-2026-KEY1', 1, false, 'DISPONIBLE');