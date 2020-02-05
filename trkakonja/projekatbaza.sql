/*
SQLyog Community v13.1.5  (64 bit)
MySQL - 10.1.35-MariaDB : Database - projekatbaza
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`projekatbaza` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `projekatbaza`;

/*Table structure for table `ishod` */

DROP TABLE IF EXISTS `ishod`;

CREATE TABLE `ishod` (
  `trkaID` int(11) NOT NULL,
  `konjID` int(11) NOT NULL,
  `mesto` int(10) DEFAULT NULL,
  PRIMARY KEY (`trkaID`,`konjID`),
  KEY `userID` (`trkaID`),
  KEY `pasID` (`konjID`),
  CONSTRAINT `ishod_ibfk_5` FOREIGN KEY (`konjID`) REFERENCES `konj` (`konjID`),
  CONSTRAINT `ishod_ibfk_6` FOREIGN KEY (`trkaID`) REFERENCES `trka` (`trkaID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `ishod` */

insert  into `ishod`(`trkaID`,`konjID`,`mesto`) values 
(1,1,2),
(1,2,1),
(2,4,2),
(2,6,1);

/*Table structure for table `konj` */

DROP TABLE IF EXISTS `konj`;

CREATE TABLE `konj` (
  `konjID` int(11) NOT NULL AUTO_INCREMENT,
  `ime` varchar(255) DEFAULT NULL,
  `boja` varchar(255) DEFAULT NULL,
  `rasa` varchar(255) DEFAULT NULL,
  `sampion` varchar(255) DEFAULT NULL,
  `slika` varchar(255) DEFAULT NULL,
  `trkacID` int(11) NOT NULL,
  PRIMARY KEY (`konjID`),
  KEY `vlasnikID_2` (`trkacID`),
  CONSTRAINT `konj_ibfk_2` FOREIGN KEY (`trkacID`) REFERENCES `trkac` (`trkacID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

/*Data for the table `konj` */

insert  into `konj`(`konjID`,`ime`,`boja`,`rasa`,`sampion`,`slika`,`trkacID`) values 
(1,'Bela griva','Braon','Engleski punokrvni','Drzavni','https://www.dnevnik.rs/sites/default/files/styles/single_article_main_image/public/2019-02/konj-engleski-punokrvnjak.jpg?itok=gbh3pOdi',1),
(2,'Zelenko','Crna','Engleski punokrvni','Svetski','https://i.pinimg.com/originals/f7/3d/37/f73d377bc87db3524b4992d395ac0322.jpg',2),
(3,'Jabucilo','Svetlo braon','Americki kasac','Evropski','https://saznajlako.com/wp-content/uploads/2012/09/Haflinger-konj-9.jpg',3),
(4,'Šarac','Braon-bela','Americki kasac','Drzavni','https://saznajlako.com/wp-content/uploads/2012/09/Americki-kasac-10.jpg',1),
(6,'Pahulja','Bela','Americki kasac','Drzavni','https://tackandtalk.files.wordpress.com/2010/12/orlov-trotter.jpg',3);

/*Table structure for table `organizator` */

DROP TABLE IF EXISTS `organizator`;

CREATE TABLE `organizator` (
  `organizatorID` int(11) NOT NULL AUTO_INCREMENT,
  `imePrezime` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`organizatorID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `organizator` */

insert  into `organizator`(`organizatorID`,`imePrezime`,`username`,`password`) values 
(1,'Milan Stankovic','milan','milan123'),
(2,'Masa Pavlovic','masa','masa123');

/*Table structure for table `trka` */

DROP TABLE IF EXISTS `trka`;

CREATE TABLE `trka` (
  `trkaID` int(11) NOT NULL AUTO_INCREMENT,
  `tip` varchar(255) DEFAULT NULL,
  `naziv` varchar(255) DEFAULT NULL,
  `grad` varchar(255) DEFAULT NULL,
  `organizatorID` int(11) DEFAULT NULL,
  PRIMARY KEY (`trkaID`),
  KEY `organizatorID` (`organizatorID`),
  CONSTRAINT `trka_ibfk_1` FOREIGN KEY (`organizatorID`) REFERENCES `organizator` (`organizatorID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

/*Data for the table `trka` */

insert  into `trka`(`trkaID`,`tip`,`naziv`,`grad`,`organizatorID`) values 
(1,'Galopska trka','Komaranska kosija','Boretino brdo',1),
(2,'Kasacka trka','Komaranska kosija','Boretino brdo',1),
(6,'Galopska trka','Drzavno prvenstvo','Subotica',2),
(7,'Kasacka trka','Gradsko prvenstvo','Subotica',1);

/*Table structure for table `trkac` */

DROP TABLE IF EXISTS `trkac`;

CREATE TABLE `trkac` (
  `trkacID` int(11) NOT NULL AUTO_INCREMENT,
  `imePrezime` varchar(255) DEFAULT NULL,
  `grad` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`trkacID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

/*Data for the table `trkac` */

insert  into `trkac`(`trkacID`,`imePrezime`,`grad`,`username`,`password`) values 
(1,'Marko Petrovic','Beograd','marko','marko123'),
(2,'Petar Markovic','Novi Sad','petar','petar123'),
(3,'Milos Pavlovic','Beograd','milos','milos123'),
(4,'Stefan Stankovic','Niš','stefan','stefan123');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
