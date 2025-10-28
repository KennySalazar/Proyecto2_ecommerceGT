
SET search_path TO ecommerce;


INSERT INTO roles (codigo, descripcion) VALUES
('COMUN','Usuario que compra y vende'),
('MODERADOR','Revisa/acepta/rechaza y sanciona'),
('LOGISTICA','Gestiona entregas'),
('ADMIN','Administra y reporta');

INSERT INTO categorias (nombre) VALUES
('Tecnología'),('Hogar'),('Académico'),('Personal'),('Decoración'),('Otro');

INSERT INTO estados_moderacion_producto (codigo, descripcion) VALUES
('PENDIENTE','Pendiente de aprobación'),
('APROBADO','Aprobado para la venta'),
('RECHAZADO','Rechazado');


INSERT INTO estados_pedido (codigo, descripcion) VALUES
('EN_CURSO','En curso'),
('ENTREGADO','Entregado');

-- 1. ADMINISTRADOR
INSERT INTO usuarios (nombre, correo, telefono, hash_password, rol_id)
VALUES
('Kenny Salazar', 'kennysalazar@ecommerce.com', '5010-0001', md5('admin123'),
 (SELECT id FROM roles WHERE codigo='ADMIN'));

-- 2. MODERADORES (5)
INSERT INTO usuarios (nombre, correo, telefono, hash_password, rol_id) VALUES
('Ana Morales',   'anamorales@ecommerce.com',   '5010-1001', md5('moderador123'), (SELECT id FROM roles WHERE codigo='MODERADOR')),
('Javier López',  'javierlopez@ecommerce.com',  '5010-1002', md5('moderador123'), (SELECT id FROM roles WHERE codigo='MODERADOR')),
('Lucía Herrera', 'luciaherrera@ecommerce.com', '5010-1003', md5('moderador123'), (SELECT id FROM roles WHERE codigo='MODERADOR')),
('Pedro Ramírez', 'pedroramirez@ecommerce.com', '5010-1004', md5('moderador123'), (SELECT id FROM roles WHERE codigo='MODERADOR')),
('María Torres',  'mariatorres@ecommerce.com',  '5010-1005', md5('moderador123'), (SELECT id FROM roles WHERE codigo='MODERADOR'));

-- 3. LOGÍSTICA (3)
INSERT INTO usuarios (nombre, correo, telefono, hash_password, rol_id) VALUES
('Luis Pérez',    'luisperez@ecommerce.com',    '5010-2001', md5('logistica123'), (SELECT id FROM roles WHERE codigo='LOGISTICA')),
('Sofía Díaz',    'sofiadiaz@ecommerce.com',    '5010-2002', md5('logistica123'), (SELECT id FROM roles WHERE codigo='LOGISTICA')),
('Fernando Ruiz', 'fernandoruiz@ecommerce.com', '5010-2003', md5('logistica123'), (SELECT id FROM roles WHERE codigo='LOGISTICA'));

-- 4. USUARIOS COMUNES (10)
INSERT INTO usuarios (nombre, correo, telefono, hash_password, rol_id) VALUES
('Andrea Castillo', 'andreacastillo@gmail.com',  '5010-3001', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Daniel Soto',     'danielsoto@gmail.com',      '5010-3002', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Gabriela León',   'gabrielaleon@gmail.com',    '5010-3003', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Héctor García',   'hectorgarcia@gmail.com',    '5010-3004', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Isabel Cruz',     'isabelcruz@gmail.com',      '5010-3005', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Jorge Martínez',  'jorgemartinez@gmail.com',   '5010-3006', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Karen López',     'karenlopez@gmail.com',      '5010-3007', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Manuel Díaz',     'manueldiaz@gmail.com',      '5010-3008', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Patricia Reyes',  'patriciareyes@gmail.com',   '5010-3009', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN')),
('Raúl Hernández',  'raulhernandez@gmail.com',   '5010-3010', md5('comun123'), (SELECT id FROM roles WHERE codigo='COMUN'));


-- Categorías usadas: 1=Tecnología, 2=Hogar, 3=Académico, 4=Personal, 5=Decoración, 6=Otro

-- ANDREA CASTILLO (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Collar artesanal','Collar hecho a mano con cuentas de colores.',7500,5,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Bolso tejido','Bolso elaborado con hilo reciclado.',9800,3,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Portarretratos de madera','Tallado a mano en madera nacional.',6000,4,'USADO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Taza pintada','Taza de cerámica decorada a mano.',4500,10,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Cartera bordada','Cartera de tela con bordado típico.',8700,2,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Pulsera de cuero','Pulsera con dijes metálicos.',5200,6,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Cuadro pequeño','Pintura de paisaje en lienzo 30x30.',12000,3,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Maceta decorativa','Maceta de cerámica pintada.',6900,4,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Porta incienso','Soporte de barro artesanal.',3800,8,'USADO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='andreacastillo@gmail.com'),'Llaveros de resina','Llaveros personalizados con resina epóxica.',5500,7,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

-- DANIEL SOTO (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Audífonos Bluetooth','Inalámbricos con estuche cargador.',120000,10,'NUEVO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Mouse inalámbrico','Óptico con receptor USB.',55000,12,'NUEVO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Teclado mecánico','RGB con switches azules.',210000,4,'NUEVO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Laptop HP 14”','Core i5, 8GB RAM, SSD 512GB.',5500000,2,'USADO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Cargador universal','Cargador rápido 20W.',80000,9,'NUEVO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Soporte para celular','Soporte de escritorio ajustable.',35000,11,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Disco externo 1TB','USB 3.0 portátil.',450000,3,'USADO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Monitor 24”','LED Full HD.',900000,4,'USADO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Cable HDMI 2m','Alta velocidad 4K.',15000,20,'NUEVO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='danielsoto@gmail.com'),'Memoria USB 64GB','Flash drive rápida.',80000,15,'NUEVO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

-- GABRIELA LEÓN (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Set de marcadores','Marcadores a base de agua.',4500,10,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Cuadernos A5','Paquete de 3 cuadernos.',6900,8,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Mochila escolar','Compartimento para laptop.',12500,5,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Resaltadores pastel','Set de 6 colores.',5200,9,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Calculadora científica','Funciones de trigonometría.',15900,4,'USADO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Porta documentos','Carpeta con cierre.',8300,6,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Plumillas para lettering','Punta flexible.',7800,7,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Atril plegable','Atril para libros.',11200,3,'USADO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Porta lápices','Metálico con separadores.',4900,10,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='gabrielaleon@gmail.com'),'Bloc de dibujo','Hoja de 180 g/m².',9800,4,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

-- HÉCTOR GARCÍA (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Termo de acero','Conserva temperatura 12h.',9500,6,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Cangurera deportiva','Bolsillo oculto.',7800,5,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Reloj análogo','Correa de cuero.',22500,2,'USADO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Lentes de sol','Filtro UV400.',11800,7,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Gorra casual','Ajustable.',5600,9,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Cartera de cuero','Bolsillos internos.',17900,3,'USADO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Cinturón café','Hebilla metálica.',9900,4,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Portadocumentos','Formato ejecutivo.',13600,2,'USADO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Set calcetines 6p','Algodón.',8700,10,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='hectorgarcia@gmail.com'),'Paraguas compacto','Apertura automática.',11900,4,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

-- ISABEL CRUZ (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Juego de sábanas','Matrimonial microfibra.',18500,3,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Cojines decorativos','Set de 2.',12500,5,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Lámpara de mesa','Brazo articulado.',14900,4,'USADO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Cortinas blackout','Par de paneles.',19900,2,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Organizador colgante','6 compartimentos.',8900,6,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Tapete antideslizante','150x80 cm.',13900,3,'USADO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Porta especias','Acero con frascos.',11500,5,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Set de toallas','2 cuerpo + 2 manos.',16900,4,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Cubertería 24p','Acero inoxidable.',23900,2,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='isabelcruz@gmail.com'),'Reloj de pared','Clásico 30cm.',9900,6,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

-- JORGE MARTÍNEZ (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Bicicleta urbana','Cuadro de aluminio.',1250000,1,'USADO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Casco ciclismo','Certificación CE.',95000,4,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Guantes deportivos','Agarre antideslizante.',45000,6,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Luz delantera','Recargable USB.',38000,5,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Cámara de acción','4K 30fps.',690000,2,'USADO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Botella deportiva','Libre de BPA.',25000,8,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Mochila hiking','50L impermeable.',289000,3,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Bomba de aire','Portátil.',32000,9,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Candado U-lock','Acero templado.',73000,4,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='jorgemartinez@gmail.com'),'Soporte de bici pared','Plegable.',59000,5,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Set brochas maquillaje','12 piezas profesionales.',159000,3,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Espejo con luz LED','Aumento 5x.',189000,2,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Plancha para cabello','Cerámica iónica.',239000,3,'USADO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Organizador acrílico','Para cosméticos.',99000,6,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Secadora de cabello','2 velocidades.',219000,2,'USADO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Neceser viaje','Impermeable.',65000,7,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Rizador 25mm','Barril cerámico.',179000,4,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Esponjas maquillaje','Paquete de 4.',49000,10,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Estuche brochas','Antipolvo.',42000,8,'NUEVO',4,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='karenlopez@gmail.com'),'Toalla turbante','Secado rápido.',37000,9,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

-- MANUEL DÍAZ (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Silla de oficina','Ergonómica malla.',899000,2,'USADO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Escritorio 120cm','Madera MDF.',749000,3,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Lámpara de piso','Cabezal ajustable.',459000,2,'USADO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Archivador 3 gavetas','Con llave.',529000,2,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Alfombra 160x200','Trama suave.',389000,4,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Organizador cables','Canaleta 1m.',99000,8,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Soporte monitor','Ajustable gas.',299000,3,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Silla visitante','Estructura metálica.',359000,2,'USADO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Pizarrón blanco','120x90 cm.',279000,5,'NUEVO',3,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='manueldiaz@gmail.com'),'Cortina roller','Screen 5%.',319000,2,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

-- PATRICIA REYES (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Centro de mesa','Madera + vidrio.',119000,3,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Jarrón cerámico','Esmaltado mate.',89000,4,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Reloj decorativo','Minimalista.',139000,2,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Cuadro abstracto','Mixto 50x70.',259000,1,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Espejo pared','Marco negro.',169000,2,'USADO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Lámpara colgante','Vintage.',219000,2,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Cojín bordado','45x45 cm.',59000,6,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Portavelas trio','Metal dorado.',74000,4,'NUEVO',5,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Cubre cama','Queen.',289000,2,'USADO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='patriciareyes@gmail.com'),'Cortinas lino','Juego 2 paneles.',199000,2,'NUEVO',2,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());

-- RAÚL HERNÁNDEZ (10)
INSERT INTO productos (vendedor_id, nombre, descripcion, precio, stock, estado_articulo, categoria_id, estado_mod_id, creado_en, actualizado_en) VALUES
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Kit destornilladores','24 puntas.',59000,8,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Taladro percutor','650W.',459000,2,'USADO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Llave inglesa','10 pulgadas.',39000,7,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Sierra caladora','Velocidad variable.',529000,2,'USADO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Nivel láser','Cruzado.',339000,3,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Caja de herramientas','19 pulgadas.',189000,4,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Guantes nitrilo','Caja 100 pzs.',49000,10,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Cinta métrica','8 metros.',29000,12,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Pistola silicón','Alta temperatura.',36000,6,'NUEVO',6,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW()),
((SELECT id FROM usuarios WHERE correo='raulhernandez@gmail.com'),'Multímetro digital','Auto rango.',169000,3,'NUEVO',1,(SELECT id FROM estados_moderacion_producto WHERE codigo='APROBADO'),NOW(),NOW());


-- IMAGENES
INSERT INTO producto_imagenes (producto_id, url, creado_en) VALUES
  (1, 'usuario1.png', NOW()),
  (2, 'usuario1.png', NOW()),
  (3, 'usuario1.png', NOW()),
  (4, 'usuario1.png', NOW()),
  (5, 'usuario1.png', NOW()),
  (6, 'usuario1.png', NOW()),
  (7, 'usuario1.png', NOW()),
  (8, 'usuario1.png', NOW()),
  (9, 'usuario1.png', NOW()),
  (10, 'usuario1.png', NOW()),
  (11, 'usuario2.png', NOW()),
  (12, 'usuario2.png', NOW()),
  (13, 'usuario2.png', NOW()),
  (14, 'usuario2.png', NOW()),
  (15, 'usuario2.png', NOW()),
  (16, 'usuario2.png', NOW()),
  (17, 'usuario2.png', NOW()),
  (18, 'usuario2.png', NOW()),
  (19, 'usuario2.png', NOW()),
  (20, 'usuario2.png', NOW()),
  (21, 'usuario3.png', NOW()),
  (22, 'usuario3.png', NOW()),
  (23, 'usuario3.png', NOW()),
  (24, 'usuario3.png', NOW()),
  (25, 'usuario3.png', NOW()),
  (26, 'usuario3.png', NOW()),
  (27, 'usuario3.png', NOW()),
  (28, 'usuario3.png', NOW()),
  (29, 'usuario3.png', NOW()),
  (30, 'usuario3.png', NOW()),
  (31, 'usuario4.png', NOW()),
  (32, 'usuario4.png', NOW()),
  (33, 'usuario4.png', NOW()),
  (34, 'usuario4.png', NOW()),
  (35, 'usuario4.png', NOW()),
  (36, 'usuario4.png', NOW()),
  (37, 'usuario4.png', NOW()),
  (38, 'usuario4.png', NOW()),
  (39, 'usuario4.png', NOW()),
  (40, 'usuario4.png', NOW()),
  (41, 'usuario5.png', NOW()),
  (42, 'usuario5.png', NOW()),
  (43, 'usuario5.png', NOW()),
  (44, 'usuario5.png', NOW()),
  (45, 'usuario5.png', NOW()),
  (46, 'usuario5.png', NOW()),
  (47, 'usuario5.png', NOW()),
  (48, 'usuario5.png', NOW()),
  (49, 'usuario5.png', NOW()),
  (50, 'usuario5.png', NOW()),
  (51, 'usuario6.png', NOW()),
  (52, 'usuario6.png', NOW()),
  (53, 'usuario6.png', NOW()),
  (54, 'usuario6.png', NOW()),
  (55, 'usuario6.png', NOW()),
  (56, 'usuario6.png', NOW()),
  (57, 'usuario6.png', NOW()),
  (58, 'usuario6.png', NOW()),
  (59, 'usuario6.png', NOW()),
  (60, 'usuario6.png', NOW()),
  (61, 'usuario7.png', NOW()),
  (62, 'usuario7.png', NOW()),
  (63, 'usuario7.png', NOW()),
  (64, 'usuario7.png', NOW()),
  (65, 'usuario7.png', NOW()),
  (66, 'usuario7.png', NOW()),
  (67, 'usuario7.png', NOW()),
  (68, 'usuario7.png', NOW()),
  (69, 'usuario7.png', NOW()),
  (70, 'usuario7.png', NOW()),
  (71, 'usuario8.png', NOW()),
  (72, 'usuario8.png', NOW()),
  (73, 'usuario8.png', NOW()),
  (74, 'usuario8.png', NOW()),
  (75, 'usuario8.png', NOW()),
  (76, 'usuario8.png', NOW()),
  (77, 'usuario8.png', NOW()),
  (78, 'usuario8.png', NOW()),
  (79, 'usuario8.png', NOW()),
  (80, 'usuario8.png', NOW()),
  (81, 'usuario9.png', NOW()),
  (82, 'usuario9.png', NOW()),
  (83, 'usuario9.png', NOW()),
  (84, 'usuario9.png', NOW()),
  (85, 'usuario9.png', NOW()),
  (86, 'usuario9.png', NOW()),
  (87, 'usuario9.png', NOW()),
  (88, 'usuario9.png', NOW()),
  (89, 'usuario9.png', NOW()),
  (90, 'usuario9.png', NOW()),
  (91, 'usuario10.png', NOW()),
  (92, 'usuario10.png', NOW()),
  (93, 'usuario10.png', NOW()),
  (94, 'usuario10.png', NOW()),
  (95, 'usuario10.png', NOW()),
  (96, 'usuario10.png', NOW()),
  (97, 'usuario10.png', NOW()),
  (98, 'usuario10.png', NOW()),
  (99, 'usuario10.png', NOW()),
  (100, 'usuario10.png', NOW());







