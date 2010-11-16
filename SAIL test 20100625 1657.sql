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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `classifier`
--

/*!40000 ALTER TABLE `classifier` DISABLE KEYS */;
INSERT INTO `classifier` (`ID`,`Name`,`Description`,`Multiple`,`Mandatory`,`Target`) VALUES 
 (26,'Vocabulary','',0,1,'PARAMETER'),
 (27,'Organ','',0,0,'PARAMETER'),
 (28,'Relation','',0,0,'RELATION'),
 (29,'История болезни','',0,0,'PARAMETER'),
 (30,'Collection annotation','',0,0,'COLLECTION_ANN');
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
-- Definition of table `collection`
--

DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Name` varchar(255) NOT NULL,
  `UpdateTime` bigint(20) unsigned NOT NULL default '0',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `collection`
--

/*!40000 ALTER TABLE `collection` DISABLE KEYS */;
INSERT INTO `collection` (`ID`,`Name`,`UpdateTime`) VALUES 
 (1,'Test cohort',1270561997234),
 (2,'Test biobank',1266415892921),
 (3,'Test cohort2',1273138600140);
/*!40000 ALTER TABLE `collection` ENABLE KEYS */;


--
-- Definition of table `collection_annotation`
--

DROP TABLE IF EXISTS `collection_annotation`;
CREATE TABLE `collection_annotation` (
  `CollectionID` int(10) unsigned NOT NULL default '0',
  `TagID` int(10) unsigned NOT NULL,
  `AnnotationText` mediumtext,
  PRIMARY KEY  USING BTREE (`CollectionID`,`TagID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `collection_annotation`
--

/*!40000 ALTER TABLE `collection_annotation` DISABLE KEYS */;
INSERT INTO `collection_annotation` (`CollectionID`,`TagID`,`AnnotationText`) VALUES 
 (2,62,'Mike\nand\nFriends');
/*!40000 ALTER TABLE `collection_annotation` ENABLE KEYS */;


--
-- Definition of table `collection_in_study`
--

DROP TABLE IF EXISTS `collection_in_study`;
CREATE TABLE `collection_in_study` (
  `CollectionID` int(10) unsigned NOT NULL default '0',
  `StudyID` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`CollectionID`,`StudyID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `collection_in_study`
--

/*!40000 ALTER TABLE `collection_in_study` DISABLE KEYS */;
INSERT INTO `collection_in_study` (`CollectionID`,`StudyID`) VALUES 
 (1,1),
 (1,2),
 (2,1);
/*!40000 ALTER TABLE `collection_in_study` ENABLE KEYS */;


--
-- Definition of table `expression`
--

DROP TABLE IF EXISTS `expression`;
CREATE TABLE `expression` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `Name` varchar(255) NOT NULL,
  `Depth` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `expression`
--

/*!40000 ALTER TABLE `expression` DISABLE KEYS */;
INSERT INTO `expression` (`ID`,`Name`,`Depth`) VALUES 
 (1,'Metabolic syndrome definition',2);
/*!40000 ALTER TABLE `expression` ENABLE KEYS */;


--
-- Definition of table `expression_content`
--

DROP TABLE IF EXISTS `expression_content`;
CREATE TABLE `expression_content` (
  `ExpressionID` int(10) unsigned NOT NULL,
  `ParameterID` int(10) unsigned NOT NULL default '0',
  `SubexpressionID` int(10) unsigned NOT NULL default '0',
  `Filter` mediumtext,
  PRIMARY KEY  (`ExpressionID`,`ParameterID`,`SubexpressionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `expression_content`
--

/*!40000 ALTER TABLE `expression_content` DISABLE KEYS */;
INSERT INTO `expression_content` (`ExpressionID`,`ParameterID`,`SubexpressionID`,`Filter`) VALUES 
 (1,173,0,NULL),
 (1,174,0,NULL);
/*!40000 ALTER TABLE `expression_content` ENABLE KEYS */;


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
) ENGINE=InnoDB AUTO_INCREMENT=215 DEFAULT CHARSET=utf8;

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
 (188,'ОНМК','острое нарушение мозгового кровообращения','острое нарушение мозгового кровообращения'),
 (189,'TAG','Test tag',''),
 (190,'TAG2','Double tagged',''),
 (191,'GW_GT','Genome-wide genotypes','Genome-wide (>100k SNPs) genotypes'),
 (192,'GW_AFFY','Affymetrix Genome-wide genotyping','Affymetrix Genome-wide genotyping'),
 (193,'GW_ILMN','Illumina Genome-wide genotyping','Illumina Genome-wide genotyping'),
 (194,'GW_AFFY_6','Affymetrix Genome-wide Human SNP Array 6.0','Affymetrix Genome-wide Human SNP Array 6.0'),
 (195,'GW_AFFY_5','Affymetrix Genome-wide Human SNP Array 5.0','Affymetrix Genome-wide Human SNP Array 5.0'),
 (196,'GW_AFFY_100k','Affymetrix Genome-wide Human SNP Array 100k Set','Affymetrix Genome-wide Human SNP Array 100k Set'),
 (197,'GW_AFFY_500k','Affymetrix Genome-wide Human SNP Array 500k Set','Affymetrix Genome-wide Human SNP Array 500k Set'),
 (198,'GW_ILMN_Human1','Illumina Human1 100k array','Illumina Human1 100k array'),
 (199,'GW_ILMN_Hap300','Illumina HumanHap300 array','Illumina HumanHap300 array'),
 (200,'GW_ILMN_Hap370','Illumina HumanHap370 array','Illumina HumanHap370 array'),
 (201,'GW_ILMN_Hap550','Illumina HumanHap550 array','Illumina HumanHap550 array'),
 (202,'GW_ILMN_Hap650','Illumina HumanHap650Y array','Illumina HumanHap650Y array'),
 (203,'GW_ILMN_iSelect','Illumina iSelect array','Illumina iSelect array'),
 (204,'GW_ILMN_660W','Illumina Human660W-Quad array','Illumina Human660W-Quad array'),
 (205,'GW_ILMN_CS12','Illumina HumanCytoSNP-12 array','Illumina HumanCytoSNP-12 array'),
 (206,'GW_ILMN_OE','Illumina HumanOmniExpress array','Illumina HumanOmniExpress array'),
 (207,'GW_ILMN_1M','Illumina Human1M-Duo array','Illumina Human1M-Duo array'),
 (208,'GW_ILMN_OMNI1','Illumina HumanOmni1-Quad array','Illumina HumanOmni1-Quad array'),
 (209,'GW_IMP','Genome-wide imputations','Genome-wide (>100k SNPs) imputations done'),
 (210,'GW_IMP_HM2','Genome-wide imputations using HapMap2','Genome-wide imputations using HapMap2'),
 (211,'GW_IMP_1kg','Genome-wide imputations using 1000 genomes','Genome-wide imputations using 1000 genomes'),
 (212,'GW_IMP_HM3','Genome-wide imputations using HapMap3','Genome-wide imputations using HapMap3'),
 (213,'BPX','Blood pressure','None'),
 (214,'LNGVX','Lung volume','Lung volume');
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
) ENGINE=InnoDB AUTO_INCREMENT=215 DEFAULT CHARSET=utf8;

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
 (188,61),
 (189,55),
 (190,55),
 (191,63),
 (192,63),
 (193,63),
 (194,63),
 (195,63),
 (196,63),
 (197,63),
 (198,63),
 (199,63),
 (200,63),
 (201,63),
 (202,63),
 (203,63),
 (204,63),
 (205,63),
 (206,63),
 (207,63),
 (208,63),
 (209,63),
 (210,63),
 (211,63),
 (212,63),
 (213,55),
 (213,56),
 (214,55),
 (214,57);
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
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8;

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
 (215,'When','','DATE',173,0,1),
 (216,'Tag','','TAG',189,0,1),
 (217,'Tag','','TAG',190,0,1),
 (218,'Number of SNPs','Number of SNPs genotyped','INTEGER',191,0,1),
 (219,'Availability',NULL,'BOOLEAN',192,0,1),
 (220,'Availability',NULL,'BOOLEAN',193,0,1),
 (221,'Availability',NULL,'BOOLEAN',194,0,1),
 (222,'Availability',NULL,'BOOLEAN',195,0,1),
 (223,'Availability',NULL,'BOOLEAN',196,0,1),
 (224,'Availability',NULL,'BOOLEAN',197,0,1),
 (225,'Availability',NULL,'BOOLEAN',198,0,1),
 (226,'Availability',NULL,'BOOLEAN',199,0,1),
 (227,'Availability',NULL,'BOOLEAN',200,0,1),
 (228,'Availability',NULL,'BOOLEAN',201,0,1),
 (229,'Availability',NULL,'BOOLEAN',202,0,1),
 (230,'Availability',NULL,'BOOLEAN',203,0,1),
 (231,'Availability',NULL,'BOOLEAN',204,0,1),
 (232,'Availability',NULL,'BOOLEAN',205,0,1),
 (233,'Availability',NULL,'BOOLEAN',206,0,1),
 (234,'Availability',NULL,'BOOLEAN',207,0,1),
 (235,'Availability',NULL,'BOOLEAN',208,0,1),
 (236,'Number of SNPs','Number of SNPs imputed','INTEGER',209,0,1),
 (237,'Number of SNPs','Number of SNPs imputed','INTEGER',210,0,1),
 (238,'Number of SNPs','Number of SNPs imputed','INTEGER',211,0,1),
 (239,'Number of SNPs','Number of SNPs imputed','INTEGER',212,0,1),
 (240,'Systolic',NULL,'INTEGER',213,0,1),
 (241,'Diastolic',NULL,'INTEGER',213,0,1),
 (242,'Volume',NULL,'REAL',214,0,1);
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
  `CollectionID` int(10) unsigned NOT NULL default '0',
  `Count` int(10) unsigned NOT NULL,
  `CollectionRecordID` mediumtext NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `record`
--

/*!40000 ALTER TABLE `record` DISABLE KEYS */;
INSERT INTO `record` (`ID`,`CollectionID`,`Count`,`CollectionRecordID`) VALUES 
 (1,1,1,'S1-01'),
 (2,1,1,'S1-02'),
 (3,1,1,'S1-03'),
 (4,1,1,'S1-04'),
 (19,2,1,'BB1'),
 (20,2,1,'BB2'),
 (21,2,1,'BB3'),
 (22,2,1,'BB4'),
 (23,2,1,'BB5'),
 (24,2,1,'BB6'),
 (25,2,1,'BB7'),
 (26,3,1,'S2-03'),
 (27,3,1,'S2-04'),
 (28,3,1,'S2-05'),
 (29,3,1,'S2-06'),
 (30,1,1,'S1'),
 (31,1,1,'S2'),
 (32,3,1,'S1X-01'),
 (33,3,1,'S1X-02'),
 (34,3,1,'S1X-03'),
 (35,3,1,'S1X-04');
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
 (2,216,NULL,NULL,NULL),
 (3,194,NULL,NULL,36.4),
 (3,195,NULL,272,NULL),
 (3,196,NULL,NULL,NULL),
 (3,197,NULL,NULL,NULL),
 (3,214,NULL,1,NULL),
 (3,215,NULL,20090201,NULL),
 (4,194,NULL,NULL,NULL),
 (4,195,NULL,273,NULL),
 (4,214,NULL,1,NULL),
 (4,215,NULL,20090303,NULL),
 (19,194,NULL,NULL,36.6),
 (19,195,NULL,272,NULL),
 (19,214,NULL,1,NULL),
 (19,215,NULL,NULL,NULL),
 (20,194,NULL,NULL,36.6),
 (20,195,NULL,272,NULL),
 (20,214,NULL,1,NULL),
 (20,215,NULL,NULL,NULL),
 (21,194,NULL,NULL,36.6),
 (21,195,NULL,272,NULL),
 (21,214,NULL,1,NULL),
 (21,215,NULL,NULL,NULL),
 (22,194,NULL,NULL,36.6),
 (22,195,NULL,272,NULL),
 (22,214,NULL,1,NULL),
 (22,215,NULL,NULL,NULL),
 (22,216,NULL,NULL,NULL),
 (23,194,NULL,NULL,36.6),
 (23,195,NULL,273,NULL),
 (23,214,NULL,1,NULL),
 (23,215,NULL,NULL,NULL),
 (23,217,NULL,NULL,NULL),
 (24,194,NULL,NULL,36.6),
 (24,195,NULL,273,NULL),
 (24,214,NULL,1,NULL),
 (24,215,NULL,NULL,NULL),
 (25,194,NULL,NULL,36.6),
 (25,195,NULL,273,NULL),
 (25,214,NULL,1,NULL),
 (25,215,NULL,NULL,NULL),
 (26,194,NULL,NULL,36.4),
 (26,195,NULL,272,NULL),
 (26,196,NULL,NULL,NULL),
 (26,197,NULL,NULL,NULL),
 (26,214,NULL,1,NULL),
 (26,215,NULL,20090201,NULL),
 (27,194,NULL,NULL,NULL),
 (27,195,NULL,273,NULL),
 (27,214,NULL,1,NULL),
 (27,215,NULL,20090303,NULL),
 (28,194,NULL,NULL,NULL),
 (28,195,NULL,0,NULL),
 (28,198,NULL,NULL,4.6),
 (28,214,NULL,1,NULL),
 (28,215,NULL,20090101,NULL),
 (29,194,NULL,NULL,36.6),
 (29,196,NULL,120,NULL),
 (29,197,NULL,80,NULL),
 (29,198,NULL,NULL,NULL),
 (29,214,NULL,0,NULL),
 (29,215,NULL,20090911,NULL),
 (30,218,NULL,3,NULL),
 (30,219,NULL,1,NULL),
 (30,220,NULL,1,NULL),
 (30,221,NULL,1,NULL),
 (30,222,NULL,1,NULL),
 (30,223,NULL,1,NULL),
 (30,224,NULL,1,NULL),
 (30,225,NULL,1,NULL),
 (30,226,NULL,1,NULL),
 (30,227,NULL,1,NULL),
 (30,228,NULL,1,NULL),
 (30,229,NULL,1,NULL),
 (30,230,NULL,1,NULL),
 (30,231,NULL,1,NULL),
 (30,232,NULL,1,NULL),
 (30,233,NULL,1,NULL),
 (30,234,NULL,1,NULL),
 (30,235,NULL,1,NULL),
 (30,236,NULL,3,NULL),
 (30,237,NULL,3,NULL),
 (30,238,NULL,3,NULL),
 (31,218,NULL,5,NULL),
 (31,219,NULL,1,NULL),
 (31,220,NULL,1,NULL),
 (31,221,NULL,1,NULL),
 (31,222,NULL,1,NULL),
 (31,223,NULL,1,NULL),
 (31,224,NULL,1,NULL),
 (31,225,NULL,1,NULL),
 (31,226,NULL,1,NULL),
 (31,227,NULL,1,NULL),
 (31,228,NULL,1,NULL),
 (31,229,NULL,1,NULL),
 (31,230,NULL,1,NULL),
 (31,231,NULL,1,NULL),
 (31,232,NULL,1,NULL),
 (31,233,NULL,1,NULL),
 (31,234,NULL,1,NULL),
 (31,235,NULL,1,NULL),
 (31,236,NULL,6,NULL),
 (31,237,NULL,8,NULL),
 (31,238,NULL,7,NULL),
 (31,239,NULL,9,NULL),
 (32,240,NULL,120,NULL),
 (32,241,NULL,80,NULL),
 (32,242,NULL,NULL,36.6),
 (33,242,NULL,NULL,4.6),
 (34,240,NULL,NULL,NULL),
 (34,241,NULL,NULL,NULL);
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
INSERT INTO `record_in_study` (`RecordID`,`StudyID`,`PostStudy`) VALUES 
 (2,1,0),
 (2,1,1),
 (3,1,0),
 (22,1,0),
 (22,1,1),
 (23,1,0),
 (23,1,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `study`
--

/*!40000 ALTER TABLE `study` DISABLE KEYS */;
INSERT INTO `study` (`ID`,`Name`,`UpdateTime`) VALUES 
 (1,'TestStudy',1266335767948),
 (2,'S2',1273497060625);
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
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;

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
 (61,'МГУ','',26),
 (62,'Owner','Collection owner',30),
 (63,'MetS','Metabolic syndrome vocabulary',26);
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
