-- Capstone Database
-- Version 1.0

-- SHOW VARIABLES;
-- SELECT @@GLOBAL.sql_mode;
-- SELECT @@SESSION.sql_mode;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;					/* Default : 1 (ON) */
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;		/* Default : 1 (ON) */


DROP SCHEMA IF EXISTS capstone;

CREATE SCHEMA capstone DEFAULT CHARACTER SET utf8;
USE capstone;

DROP TABLE IF EXISTS user_info;
DROP TABLE IF EXISTS conn_info;

-------------------------------------------
-- Schema
-------------------------------------------
CREATE TABLE user_info (
    Id      CHAR(15)    NOT NULL,
    Pw      CHAR(15)    NOT NULL,
    Name    CHAR(10)    NOT NULL,
    Email   CHAR(30)    NOT NULL,

    PRIMARY KEY (Id),
    INDEX   idx_id      (Id ASC)
);

CREATE TABLE conn_info (
    Id          CHAR(15)    NOT NULL,
    MacAddr     CHAR(17)    NOT NULL,
    DeviceName  CHAR(10)    		,
    DeviceType	Int(10)		NOT NULL,

    PRIMARY KEY     (Id, MacAddr),
    INDEX   idx_id      (Id ASC),
    INDEX   idx_Mac     (MacAddr ASC),

    CONSTRAINT      fk_user_conn    FOREIGN KEY (Id)    REFERENCES user_info(Id)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
);

-------------------------------------------
-- Data
-------------------------------------------

INSERT INTO user_info VALUES
('test', 'test', 'test', 'test@mail.com'),
('test2', 'test', 'test', 'test@mail.com'),
('test3', 'test', 'test', 'test@mail.com');

INSERT INTO conn_info VALUES
('test', 'ff:ff:ff:ff:ff:ff', 'test', 1),
('test', 'ff:ff:ff:ff:ff:ee', 'test', 1),
('test2', 'ff:ff:ff:ff:ff:ff', 'test', 1),
('test3', 'ff:ff:ff:ff:ff:ff', 'test', 1);



-- SET SQL_MODE=@OLD_SQL_MODE;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;