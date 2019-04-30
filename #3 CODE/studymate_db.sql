-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.2.14-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- studymate 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `studymate` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `studymate`;

-- 테이블 studymate.chapter 구조 내보내기
CREATE TABLE IF NOT EXISTS `chapter` (
  `chapterID` int(11) NOT NULL AUTO_INCREMENT,
  `chapterName` varchar(100) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `subjectID` int(11) DEFAULT NULL,
  PRIMARY KEY (`chapterID`),
  KEY `subjectID` (`subjectID`),
  CONSTRAINT `chapter_ibfk_1` FOREIGN KEY (`subjectID`) REFERENCES `subject` (`subjectID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- 테이블 데이터 studymate.chapter:~7 rows (대략적) 내보내기
DELETE FROM `chapter`;
/*!40000 ALTER TABLE `chapter` DISABLE KEYS */;
INSERT INTO `chapter` (`chapterID`, `chapterName`, `timestamp`, `subjectID`) VALUES
	(10, '심리학이란 무엇인가', '2018-05-19 16:03:01', 2),
	(11, '생물학과 행동', '2018-05-19 16:03:17', 2),
	(12, '감각과 지각', '2018-05-19 16:03:27', 2),
	(13, '의식', '2018-05-19 16:03:33', 2),
	(14, '학습', '2018-05-19 16:03:39', 2),
	(15, '기억', '2018-05-19 16:03:45', 2),
	(16, '인지와 언어, 지능', '2018-05-19 16:03:57', 2);
/*!40000 ALTER TABLE `chapter` ENABLE KEYS */;

-- 테이블 studymate.course 구조 내보내기
CREATE TABLE IF NOT EXISTS `course` (
  `courseID` varchar(100) NOT NULL,
  `credit` varchar(100) DEFAULT NULL,
  `credit_theory` varchar(100) DEFAULT NULL,
  `credit_practice` varchar(100) DEFAULT NULL,
  `courseName` varchar(100) DEFAULT NULL,
  `classification` varchar(100) DEFAULT NULL,
  `classtime` varchar(100) DEFAULT NULL,
  `classroom` varchar(100) DEFAULT NULL,
  `school` varchar(100) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `professor` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`courseID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 studymate.course:~3 rows (대략적) 내보내기
DELETE FROM `course`;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` (`courseID`, `credit`, `credit_theory`, `credit_practice`, `courseName`, `classification`, `classtime`, `classroom`, `school`, `department`, `professor`) VALUES
	('CLTR038-001', '3.0', '3.0', '0.0', '스페인어', '기본소양', '월1A1B2A\n월2B3A3B\n화1A1B2A\n화2B3A3B\n수1A1B2A\n수2B3A3B\n금1A1B2A\n금2B3A3B', '인문대학313', '인문대학', '사학과', '김선웅'),
	('CLTR057-004', '2.0', '2.0', '0.0', '중급교양영어', '기본소양', '월5A5B\n월6A6B\n화5A5B\n화6A6B\n수5A5B\n수6A6B\n목5A5B\n목6A6B\n금5A5B\n금6A6B', '외국어교육관413', '교육개발본부', '교양교육센터', '쿡, 콜린 제임스'),
	('CLTR057-010', '2.0', '2.0', '0.0', '중급교양영어', '기본소양', '월7A7B\n월8A8B\n화7A7B\n화8A8B\n수7A7B\n수8A8B\n목7A7B\n목8A8B\n금7A7B\n금8A8B', '외국어교육관424', '교육개발본부', '교양교육센터', '맨티, 타샤 리앤');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;

-- 테이블 studymate.file 구조 내보내기
CREATE TABLE IF NOT EXISTS `file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(50) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- 테이블 데이터 studymate.file:~0 rows (대략적) 내보내기
DELETE FROM `file`;
/*!40000 ALTER TABLE `file` DISABLE KEYS */;
/*!40000 ALTER TABLE `file` ENABLE KEYS */;

-- 테이블 studymate.schedule 구조 내보내기
CREATE TABLE IF NOT EXISTS `schedule` (
  `scheduleID` int(11) NOT NULL AUTO_INCREMENT,
  `scheduleName` varchar(50) DEFAULT NULL,
  `color` varchar(50) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `startTime` time DEFAULT NULL,
  `endTime` time DEFAULT NULL,
  `checkState` varchar(50) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `d_day` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`scheduleID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;

-- 테이블 데이터 studymate.schedule:~8 rows (대략적) 내보내기
DELETE FROM `schedule`;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
INSERT INTO `schedule` (`scheduleID`, `scheduleName`, `color`, `date`, `startTime`, `endTime`, `checkState`, `comment`, `d_day`) VALUES
	(19, '기업가정신과 벤처창업 시험', '#6aa549', '2018-06-17', '14:00:00', '15:00:00', 'N', '등록된 내용이 없습니다.', 'N'),
	(20, '계절하으악기', '#fe0001', '2018-06-25', '09:00:00', '16:00:00', 'N', '계절하으악기', 'Y'),
	(21, '공학수학 시험', '#1099cb', '2018-06-19', '18:00:00', '21:00:00', 'N', '공학수학시험', 'Y'),
	(22, '알고리즘 시험', '#6aa549', '2018-06-08', '10:30:00', '12:30:00', 'Y', '9호관 ', 'N'),
	(23, '일반생명과학시험', '#9c01ff', '2018-06-08', '15:00:00', '16:30:00', 'N', '9호관', 'N'),
	(24, '소프트웨어공학 시험', '#000000', '2018-06-12', '13:30:00', '15:00:00', 'N', '등록된 내용이 없습니다.', 'N'),
	(25, '소프트웨어공학 시연회', '#000000', '2018-06-19', '13:30:00', '15:00:00', 'N', '등록된 내용이 없습니다.', 'N'),
	(26, '지도교수 면담', '#9c01ff', '2018-06-18', '00:00:00', '00:00:00', 'N', '등록된 내용이 없습니다.', 'Y');
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;

-- 테이블 studymate.subject 구조 내보내기
CREATE TABLE IF NOT EXISTS `subject` (
  `subjectID` int(11) NOT NULL AUTO_INCREMENT,
  `subjectName` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`subjectID`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;

-- 테이블 데이터 studymate.subject:~16 rows (대략적) 내보내기
DELETE FROM `subject`;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` (`subjectID`, `subjectName`) VALUES
	(2, '심리학의이해'),
	(7, 'asdfasdf'),
	(8, 'czccv'),
	(11, 'ADSFSADF'),
	(12, '테스트2'),
	(13, '테스트2'),
	(21, '수정쓰'),
	(22, '테스트33'),
	(25, '테스트4'),
	(26, 'ㅁㅇㄴㄻㄴㄹ'),
	(30, 'ㅁㅇㄹ'),
	(34, 'zz'),
	(41, '테스트'),
	(42, 'sdf'),
	(43, 'SDFSDAF'),
	(46, 'asdf');
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;

-- 테이블 studymate.vocabulary 구조 내보내기
CREATE TABLE IF NOT EXISTS `vocabulary` (
  `vocaID` int(11) NOT NULL AUTO_INCREMENT,
  `voca` varchar(50) DEFAULT NULL,
  `mean` varchar(200) DEFAULT NULL,
  `chapterID` int(11) DEFAULT NULL,
  PRIMARY KEY (`vocaID`),
  KEY `chapterID` (`chapterID`),
  CONSTRAINT `vocabulary_ibfk_1` FOREIGN KEY (`chapterID`) REFERENCES `chapter` (`chapterID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

-- 테이블 데이터 studymate.vocabulary:~9 rows (대략적) 내보내기
DELETE FROM `vocabulary`;
/*!40000 ALTER TABLE `vocabulary` DISABLE KEYS */;
INSERT INTO `vocabulary` (`vocaID`, `voca`, `mean`, `chapterID`) VALUES
	(8, '구성주의', '최초의 심리학파, 의식적인 정신 경험의 구성 요소나 구조를 분석하는 것이 목표.1', 10),
	(9, '기능주의', '윌리엄 제임스가 주창. 의식의 구조가 아닌 정신 과정의 기능에 집중', 10),
	(10, '행동주의', '대표적인 심리학자 존 왓슨. 심리학을 \'행동의 과학\' 으로 정의하며, 관찰하고 측정할 수 있는 행동만을 연구', 10),
	(11, '정신분석학', '의식적인 정신 경험은 눈에 보이는 아주 작은 부분이고, 그 아래 방대한 무의식이 있다는 심리학 이론. 대표적인 심리학자 지그문트 프로이트asdf111', 10),
	(27, 'asdf', 'asd1', 10),
	(32, 'ㅇㅇ', 'asdf', 10),
	(33, 'ㅇ', 'asdfasdfads', 10),
	(34, 'asdf', 'asdfasdf', 10),
	(35, 'd', 'f', 10);
/*!40000 ALTER TABLE `vocabulary` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
