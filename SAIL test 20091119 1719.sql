-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.51b-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema sail_test4
--

CREATE DATABASE IF NOT EXISTS sail_test4;
USE sail_test4;

--
-- Definition of table `classifier`
--

DROP TABLE IF EXISTS `classifier`;
CREATE TABLE `classifier` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Name` varchar(255) NOT NULL,
  `Description` mediumtext NOT NULL,
  `Multiple` tinyint(1) NOT NULL,
  `Mandatory` tinyint(1) NOT NULL,
  `Target` varchar(45) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `classifier`
--

/*!40000 ALTER TABLE `classifier` DISABLE KEYS */;
INSERT INTO `classifier` (`ID`,`Name`,`Description`,`Multiple`,`Mandatory`,`Target`) VALUES 
 (26,'Vocabulary','',0,1,'PARAMETER'),
 (27,'Organ','',0,0,'PARAMETER'),
 (28,'Relation','',0,0,'RELATION'),
 (29,'История болезни','',0,0,'PARAMETER');
/*!40000 ALTER TABLE `classifier` ENABLE KEYS */;


--
-- Definition of table `classifier_classification`
--

DROP TABLE IF EXISTS `classifier_classification`;
CREATE TABLE `classifier_classification` (
  `ClassifierID` int(10) unsigned NOT NULL auto_increment,
  `TagID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`ClassifierID`,`TagID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `classifier_classification`
--

/*!40000 ALTER TABLE `classifier_classification` DISABLE KEYS */;
/*!40000 ALTER TABLE `classifier_classification` ENABLE KEYS */;


--
-- Definition of table `cohort`
--

DROP TABLE IF EXISTS `cohort`;
CREATE TABLE `cohort` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Name` varchar(255) NOT NULL,
  `UpdateTime` bigint(20) unsigned NOT NULL default '0',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cohort`
--

/*!40000 ALTER TABLE `cohort` DISABLE KEYS */;
INSERT INTO `cohort` (`ID`,`Name`,`UpdateTime`) VALUES 
 (1,'Test cohort',1258561471125);
/*!40000 ALTER TABLE `cohort` ENABLE KEYS */;


--
-- Definition of table `cohort_annotation`
--

DROP TABLE IF EXISTS `cohort_annotation`;
CREATE TABLE `cohort_annotation` (
  `CohortID` int(10) unsigned NOT NULL default '0',
  `TagID` int(10) unsigned NOT NULL,
  `AnnotationText` mediumtext,
  PRIMARY KEY  USING BTREE (`CohortID`,`TagID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cohort_annotation`
--

/*!40000 ALTER TABLE `cohort_annotation` DISABLE KEYS */;
/*!40000 ALTER TABLE `cohort_annotation` ENABLE KEYS */;


--
-- Definition of table `cohort_in_study`
--

DROP TABLE IF EXISTS `cohort_in_study`;
CREATE TABLE `cohort_in_study` (
  `CohortID` int(10) unsigned NOT NULL,
  `StudyID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`CohortID`,`StudyID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `cohort_in_study`
--

/*!40000 ALTER TABLE `cohort_in_study` DISABLE KEYS */;
/*!40000 ALTER TABLE `cohort_in_study` ENABLE KEYS */;


--
-- Definition of table `inherited`
--

DROP TABLE IF EXISTS `inherited`;
CREATE TABLE `inherited` (
  `HostParameterID` int(10) unsigned NOT NULL,
  `TargetParameterID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`HostParameterID`,`TargetParameterID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `inherited`
--

/*!40000 ALTER TABLE `inherited` DISABLE KEYS */;
/*!40000 ALTER TABLE `inherited` ENABLE KEYS */;


--
-- Definition of table `parameter`
--

DROP TABLE IF EXISTS `parameter`;
CREATE TABLE `parameter` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Code` varchar(45) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Description` mediumtext,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `code_idx` (`Code`)
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `parameter`
--

/*!40000 ALTER TABLE `parameter` DISABLE KEYS */;
INSERT INTO `parameter` (`ID`,`Code`,`Name`,`Description`) VALUES 
 (173,'TEMP','Temperature','Body temperature'),
 (174,'BP','Blood pressure','Desc line 1\nDesc line 2'),
 (175,'LNGV','Lung volume','Lung volume'),
 (176,'ANTH:TMP','Temperature',''),
 (177,'Атеросклероз','Атеросклероз аорты и коронар. артерий','Атеросклероз'),
 (178,'стенокардия напряжения','стенокардия напряжения','стенокардия напряжения'),
 (179,'ОИМ','острый инфаркт миокарда','острый инфаркт миокарда'),
 (180,'ОИМ2','повторный инфаркт миокарда','повторный инфаркт миокарда'),
 (181,'постинфарктный кардиосклероз','постинфарктный кардиосклероз','постинфарктный кардиосклероз'),
 (182,'нарушение ритма','нарушение ритма','нарушение ритма'),
 (183,'ИКМП','ишемическая кардиомиопатия','ишемическая кардиомиопатия'),
 (184,'аневризма ЛЖ','аневризма левого желудочка','аневризма левого желудочка'),
 (185,'безболевая ишемия миокарда','безболевая ишемия миокарда','безболевая ишемия миокарда'),
 (186,'ХСН','хроническая сердечная недостаточность','хроническая сердечная недостаточность четырех функциональных классов (фк.) по классификации NYHA'),
 (187,'АГ','артериальная гипертензия','артериальная гипертензия'),
 (188,'ОНМК','острое нарушение мозгового кровообращения','острое нарушение мозгового кровообращения');
/*!40000 ALTER TABLE `parameter` ENABLE KEYS */;


--
-- Definition of table `parameter_annotation`
--

DROP TABLE IF EXISTS `parameter_annotation`;
CREATE TABLE `parameter_annotation` (
  `ParameterID` int(10) unsigned NOT NULL,
  `TagID` int(10) unsigned NOT NULL,
  `AnnotationText` mediumtext,
  PRIMARY KEY  USING BTREE (`ParameterID`,`TagID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `parameter_annotation`
--

/*!40000 ALTER TABLE `parameter_annotation` DISABLE KEYS */;
/*!40000 ALTER TABLE `parameter_annotation` ENABLE KEYS */;


--
-- Definition of table `parameter_classification`
--

DROP TABLE IF EXISTS `parameter_classification`;
CREATE TABLE `parameter_classification` (
  `ParameterID` int(10) unsigned NOT NULL auto_increment,
  `TagID` int(10) unsigned NOT NULL,
  PRIMARY KEY  USING BTREE (`ParameterID`,`TagID`)
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `parameter_classification`
--

/*!40000 ALTER TABLE `parameter_classification` DISABLE KEYS */;
INSERT INTO `parameter_classification` (`ParameterID`,`TagID`) VALUES 
 (173,55),
 (174,55),
 (174,56),
 (175,55),
 (175,57),
 (176,58),
 (177,60),
 (177,61),
 (178,60),
 (178,61),
 (179,60),
 (179,61),
 (180,60),
 (180,61),
 (181,60),
 (181,61),
 (182,60),
 (182,61),
 (183,60),
 (183,61),
 (184,60),
 (184,61),
 (185,60),
 (185,61),
 (186,60),
 (186,61),
 (187,60),
 (187,61),
 (188,60),
 (188,61);
/*!40000 ALTER TABLE `parameter_classification` ENABLE KEYS */;


--
-- Definition of table `part`
--

DROP TABLE IF EXISTS `part`;
CREATE TABLE `part` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Name` varchar(255) NOT NULL,
  `Description` mediumtext,
  `Type` varchar(45) NOT NULL,
  `ParameterID` int(10) unsigned NOT NULL default '0',
  `Predefined` tinyint(1) NOT NULL,
  `Mandatory` tinyint(1) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `part`
--

/*!40000 ALTER TABLE `part` DISABLE KEYS */;
INSERT INTO `part` (`ID`,`Name`,`Description`,`Type`,`ParameterID`,`Predefined`,`Mandatory`) VALUES 
 (194,'Temp','','REAL',173,0,1),
 (195,'Place','Place of measurement','QUALIFIER',173,0,0),
 (196,'Systolic',NULL,'INTEGER',174,0,1),
 (197,'Diastolic',NULL,'INTEGER',174,0,1),
 (198,'Volume',NULL,'REAL',175,0,1),
 (199,'Temperature','','REAL',176,0,1),
 (200,'Атеросклероз',NULL,'ENUM',177,0,1),
 (201,'стенокардия напряжения',NULL,'ENUM',178,0,1),
 (202,'ОИМ',NULL,'DATE',179,0,1),
 (203,'ОИМ 2',NULL,'DATE',180,0,1),
 (204,'постинфарктный кардиосклероз',NULL,'ENUM',181,0,1),
 (205,'нарушение ритма',NULL,'ENUM',182,0,1),
 (206,'ИКМП',NULL,'ENUM',183,0,1),
 (207,'аневризма ЛЖ',NULL,'ENUM',184,0,1),
 (208,'безболевая ишемия миокарда',NULL,'ENUM',185,0,1),
 (209,'стенокардия напряжения',NULL,'ENUM',186,0,1),
 (210,'степень',NULL,'ENUM',187,0,1),
 (211,'риск',NULL,'ENUM',187,0,1),
 (212,'острое нарушение мозгового кровообращения',NULL,'ENUM',188,0,1),
 (213,'год',NULL,'QUALIFIER',188,0,1),
 (214,'IsGood','','BOOLEAN',173,0,1),
 (215,'When','','DATE',173,0,1);
/*!40000 ALTER TABLE `part` ENABLE KEYS */;


--
-- Definition of table `projection`
--

DROP TABLE IF EXISTS `projection`;
CREATE TABLE `projection` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Name` varchar(255) NOT NULL default '',
  `Description` mediumtext,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `Index_Name` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `projection`
--

/*!40000 ALTER TABLE `projection` DISABLE KEYS */;
INSERT INTO `projection` (`ID`,`Name`,`Description`) VALUES 
 (1,'история болезни','');
/*!40000 ALTER TABLE `projection` ENABLE KEYS */;


--
-- Definition of table `projection_content`
--

DROP TABLE IF EXISTS `projection_content`;
CREATE TABLE `projection_content` (
  `ProjectionID` int(10) unsigned NOT NULL,
  `ClassifierID` int(10) unsigned NOT NULL,
  `ClassifierOrder` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`ProjectionID`,`ClassifierID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `projection_content`
--

/*!40000 ALTER TABLE `projection_content` DISABLE KEYS */;
INSERT INTO `projection_content` (`ProjectionID`,`ClassifierID`,`ClassifierOrder`) VALUES 
 (1,29,1);
/*!40000 ALTER TABLE `projection_content` ENABLE KEYS */;


--
-- Definition of table `record`
--

DROP TABLE IF EXISTS `record`;
CREATE TABLE `record` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `CohortID` int(10) unsigned NOT NULL default '0',
  `Count` int(10) unsigned NOT NULL,
  `CohortRecordID` mediumtext NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `record`
--

/*!40000 ALTER TABLE `record` DISABLE KEYS */;
INSERT INTO `record` (`ID`,`CohortID`,`Count`,`CohortRecordID`) VALUES 
 (1,1,1,'S1-01'),
 (2,1,1,'S1-02'),
 (3,1,1,'S1-03'),
 (4,1,1,'S1-04');
/*!40000 ALTER TABLE `record` ENABLE KEYS */;


--
-- Definition of table `record_content`
--

DROP TABLE IF EXISTS `record_content`;
CREATE TABLE `record_content` (
  `RecordID` int(10) unsigned NOT NULL,
  `PartID` int(10) unsigned NOT NULL,
  `EnumValue` varchar(255) default NULL,
  `ValueInt` int(11) default NULL,
  `ValueReal` double default NULL,
  PRIMARY KEY  (`RecordID`,`PartID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `record_content`
--

/*!40000 ALTER TABLE `record_content` DISABLE KEYS */;
INSERT INTO `record_content` (`RecordID`,`PartID`,`EnumValue`,`ValueInt`,`ValueReal`) VALUES 
 (1,194,NULL,NULL,36.6),
 (1,196,NULL,120,NULL),
 (1,197,NULL,80,NULL),
 (1,198,NULL,NULL,NULL),
 (1,214,NULL,0,NULL),
 (1,215,NULL,20090911,NULL),
 (2,194,NULL,NULL,NULL),
 (2,195,NULL,0,NULL),
 (2,198,NULL,NULL,4.6),
 (2,214,NULL,1,NULL),
 (2,215,NULL,20090101,NULL),
 (3,194,NULL,NULL,36.4),
 (3,195,NULL,272,NULL),
 (3,196,NULL,NULL,NULL),
 (3,197,NULL,NULL,NULL),
 (3,214,NULL,1,NULL),
 (3,215,NULL,20090201,NULL),
 (4,194,NULL,NULL,NULL),
 (4,195,NULL,273,NULL),
 (4,214,NULL,1,NULL),
 (4,215,NULL,20090303,NULL);
/*!40000 ALTER TABLE `record_content` ENABLE KEYS */;


--
-- Definition of table `record_in_study`
--

DROP TABLE IF EXISTS `record_in_study`;
CREATE TABLE `record_in_study` (
  `RecordID` int(10) unsigned NOT NULL,
  `StudyID` int(10) unsigned NOT NULL,
  `PostStudy` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`RecordID`,`StudyID`,`PostStudy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `record_in_study`
--

/*!40000 ALTER TABLE `record_in_study` DISABLE KEYS */;
/*!40000 ALTER TABLE `record_in_study` ENABLE KEYS */;


--
-- Definition of table `relation`
--

DROP TABLE IF EXISTS `relation`;
CREATE TABLE `relation` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `HostParameterID` int(10) unsigned NOT NULL,
  `TargetParameterID` int(10) unsigned NOT NULL,
  `TagID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `relation`
--

/*!40000 ALTER TABLE `relation` DISABLE KEYS */;
INSERT INTO `relation` (`ID`,`HostParameterID`,`TargetParameterID`,`TagID`) VALUES 
 (1,176,173,59),
 (2,173,176,59);
/*!40000 ALTER TABLE `relation` ENABLE KEYS */;


--
-- Definition of table `study`
--

DROP TABLE IF EXISTS `study`;
CREATE TABLE `study` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Name` varchar(255) NOT NULL,
  `UpdateTime` bigint(20) unsigned NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `study`
--

/*!40000 ALTER TABLE `study` DISABLE KEYS */;
/*!40000 ALTER TABLE `study` ENABLE KEYS */;


--
-- Definition of table `study_annotation`
--

DROP TABLE IF EXISTS `study_annotation`;
CREATE TABLE `study_annotation` (
  `StudyID` int(10) unsigned NOT NULL,
  `TagID` int(10) unsigned NOT NULL,
  `AnnotationText` mediumtext NOT NULL,
  PRIMARY KEY  (`StudyID`,`TagID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `study_annotation`
--

/*!40000 ALTER TABLE `study_annotation` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_annotation` ENABLE KEYS */;


--
-- Definition of table `tag`
--

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Name` varchar(255) NOT NULL,
  `Description` mediumtext,
  `ClassifierID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tag`
--

/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` (`ID`,`Name`,`Description`,`ClassifierID`) VALUES 
 (55,'TestVoc','',26),
 (56,'Blood','',27),
 (57,'Lung','',27),
 (58,'Another','',26),
 (59,'Synonym','',28),
 (60,'Основной диагноз','',29),
 (61,'МГУ','',26);
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;


--
-- Definition of table `variant`
--

DROP TABLE IF EXISTS `variant`;
CREATE TABLE `variant` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `PartID` int(10) unsigned NOT NULL default '0',
  `Name` varchar(255) NOT NULL default '""',
  `Coding` int(11) NOT NULL,
  `Predefined` tinyint(1) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=309 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `variant`
--

/*!40000 ALTER TABLE `variant` DISABLE KEYS */;
INSERT INTO `variant` (`ID`,`PartID`,`Name`,`Coding`,`Predefined`) VALUES 
 (272,195,'Armpit',1,1),
 (273,195,'Mouth',2,1),
 (274,200,'да',1,1),
 (275,200,'нет',0,1),
 (276,201,'нет',0,1),
 (277,201,'I фк',1,1),
 (278,201,'II фк',2,1),
 (279,201,'III фк',3,1),
 (280,201,'IV фк',4,1),
 (281,204,'нет',0,1),
 (282,204,'однокр',1,1),
 (283,204,'двукр',2,1),
 (284,205,'нет',1,1),
 (285,205,'ЖЭС',2,1),
 (286,205,'НЖЭС',3,1),
 (287,205,'НЖТ',4,1),
 (288,205,'ПФМП',5,1),
 (289,205,'ПФТП',6,1),
 (290,206,'нет',0,1),
 (291,206,'да',1,1),
 (292,207,'нет',0,1),
 (293,207,'да',1,1),
 (294,208,'нет',0,1),
 (295,208,'да',1,1),
 (296,209,'I фк',0,1),
 (297,209,'II фк',1,1),
 (298,209,'III фк',2,1),
 (299,209,'IV фк',3,1),
 (300,210,'I',1,1),
 (301,210,'II',2,1),
 (302,210,'III',3,1),
 (303,211,'1',1,1),
 (304,211,'2',2,1),
 (305,211,'3',3,1),
 (306,211,'4',4,1),
 (307,212,'да',1,1),
 (308,212,'нет',0,1);
/*!40000 ALTER TABLE `variant` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
