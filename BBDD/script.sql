-- -----------------------------------------------------
-- Table `cuantossomos`.`posiciones`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cuantossomos`.`posiciones` ;

CREATE  TABLE IF NOT EXISTS `cuantossomos`.`posiciones` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `pais` VARCHAR(500) NOT NULL ,
  `areaAdministrativa` VARCHAR(500) NOT NULL ,
  `subAreaAdministrativa` VARCHAR(500) NOT NULL ,
  `localidad` VARCHAR(500) NOT NULL ,
  `subLocalidad` VARCHAR(500) NOT NULL ,
  `calle` VARCHAR(500) NOT NULL ,
  `osm_id` BIGINT NULL ,
  `place_id` BIGINT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cuantossomos`.`posiciones_usuarios`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cuantossomos`.`posiciones_usuarios` ;

CREATE  TABLE IF NOT EXISTS `cuantossomos`.`posiciones_usuarios` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `idPosicion` INT NOT NULL ,
  `idUsuario` VARCHAR(500) NOT NULL ,
  `fecha` DATETIME NOT NULL ,
  `latitud` DOUBLE NOT NULL ,
  `longitud` DOUBLE NOT NULL ,
  `plataforma` VARCHAR(10) ,
  PRIMARY KEY (`id`) ,
  INDEX `posicion_fk` (`idPosicion` ASC) ,
  CONSTRAINT `posicion_fk`
    FOREIGN KEY (`idPosicion` )
    REFERENCES `cuantossomos`.`posiciones` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;