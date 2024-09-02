CREATE DATABASE  IF NOT EXISTS `usuarios`;
USE `usuarios`;

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario`(
  `rol` varchar(10) NOT NULL,
  `nombre` varchar(25) NOT NULL,
  `apellidos` varchar(50) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `contrasena` varchar(20) NOT NULL,
  `equipo` varchar(255),
  PRIMARY KEY (`correo`)
  -- FOREIGN KEY equipo REFERENCES equipo
);

INSERT INTO `usuarios`.`usuario`
VALUES
('admin', 'admin', 'admin', 'admin@admin.com', 'admin', null
),
('user', 'Jose', 'Gonzalez', 'jgonzalez@gmail.com', 'jgrbb', 'Real Betis'
);

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS plantillasJugadores;

-- Seleccionar la base de datos
USE plantillasJugadores;

-- Eliminar la tabla si existe
DROP TABLE IF EXISTS Jugador;
DROP TABLE IF EXISTS Equipo;


CREATE TABLE Equipo (
    nombre VARCHAR(255) PRIMARY KEY,
    foto LONGBLOB, -- Using LONGBLOB for storing images
    posicion INTEGER NOT NULL CHECK (posicion BETWEEN 1 AND 20)
);

CREATE TABLE Jugador (
    dni VARCHAR(9) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    alias VARCHAR(255) NOT NULL,
    posicion ENUM('delantero', 'defensa', 'medio', 'portero') NOT NULL,
    equipo VARCHAR(255) NOT NULL,
    ruta_foto VARCHAR(255) NOT NULL
);

INSERT INTO `plantillasJugadores`.`Jugador` 
VALUES(
'00000000A',
'Lionel',
'Messi',
'Messi',
'delantero',
'Barcelona FC',
'https://pbs.twimg.com/media/EgSn9cNWsAc5kDy.png'
),
(
'00000001B',
'Cristiano',
'Ronaldo',
'El bicho',
'delantero',
'Real Madrid FC',
'https://i.imgur.com/MWmnN1E.png'
),
(
'00000001C',
'Karim',
'Benzema',
'Benzema',
'delantero',
'Real Madrid FC',
'https://i.goalzz.com/?i=o%2Fp%2F21%2F490%2Fkarim-benzema-1.png'
),
(
'00000001D',
'Gareth',
'Bale',
'Bale',
'delantero',
'Real Madrid FC',
'https://cdn.futwiz.com/assets/img/fifa20/faces/173731.png'
),
(
'00000001E',
'James',
'Rodríguez',
'James',
'delantero',
'Real Madrid FC',
'https://sortitoutsidospaces.b-cdn.net/megapacks/cutoutfaces/originals/10.09/76002390.png'
)