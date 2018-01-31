-- phpMyAdmin SQL Dump
-- version 2.11.5.1
-- http://www.phpmyadmin.net
--
-- 호스트: localhost
-- 처리한 시간: 17-10-31 15:34 
-- 서버 버전: 5.5.17
-- PHP 버전: 5.2.17p1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- 데이터베이스: `kok99274`
--
CREATE DATABASE `kok99274` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `kok99274`;

-- --------------------------------------------------------

--
-- 테이블 구조 `CHECKCONNECT`
--

CREATE TABLE IF NOT EXISTS `CHECKCONNECT` (
  `TOURIST_ID` varchar(30) NOT NULL,
  `GUIDE_ID` varchar(30) NOT NULL,
  `CONNECTED` int(11) NOT NULL,
  `CONNECTDATE` date NOT NULL,
  `AREA` varchar(30) NOT NULL,
  `MSG` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- 테이블의 덤프 데이터 `CHECKCONNECT`
--


-- --------------------------------------------------------

--
-- 테이블 구조 `MEMBERINFO`
--

CREATE TABLE IF NOT EXISTS `MEMBERINFO` (
  `MEM_ID` varchar(30) NOT NULL,
  `MEM_NAME` varchar(30) NOT NULL,
  `MEM_DOB` date NOT NULL,
  `MEM_GENDER` varchar(30) NOT NULL,
  `MEM_EMAIL` varchar(30) NOT NULL,
  `MEM_PHONE` varchar(30) NOT NULL,
  `MEM_NATIONALITY` varchar(30) NOT NULL,
  `MEM_LANGUAGE` varchar(30) NOT NULL,
  `MEM_PUSHKEY` text NOT NULL,
  `ACCESS_TOKEN` text NOT NULL,
  `MEM_LINK` varchar(30) NOT NULL,
  `USER_CNUM` int(11) NOT NULL,
  `MEM_PR` varchar(30) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- 테이블의 덤프 데이터 `MEMBERINFO`
--

INSERT INTO `MEMBERINFO` (`MEM_ID`, `MEM_NAME`, `MEM_DOB`, `MEM_GENDER`, `MEM_EMAIL`, `MEM_PHONE`, `MEM_NATIONALITY`, `MEM_LANGUAGE`, `MEM_PUSHKEY`, `ACCESS_TOKEN`, `MEM_LINK`, `USER_CNUM`, `MEM_PR`) VALUES
('1977874765760532', '이승호', '1985-02-14', 'male', '1977874765760532', '01012345678', 'korea', 'korean', 'ejhz9ezu9Gk:APA91bEmSV2KKatBbr5F0Q5yBS220SBpSGKnNn9TpcuzSHFjrN1iN6H9pvrTe-3t8tXas-vJGFCsWgemZdvWpBVTOzUU_1pj279NHgGxWn2H5zCDoRL0d37t7iEhGnQTA4MJHOz-tC-s', 'EAAbOY1vfAAYBAIq6RlNEsZBwCCZAqfhvwglSZBq3LZBulZCOJ4nwK4o6ZBdwxvP4EJpFZCd25jOaULYWBUoaTQXz34sUUySk6n7FwGOFnV71ZAzFHZCMnY9yWlAMjzJtDEg5zGxRziWF3TP3z1MCsAWdJm9BgSiyjPdZAQJ9fh58zd7Yq0pJXiWFweXNXeX8aZBc6pciOZCbg9Ihoyg3BhYsZBNNV', 'disabled', 1, 'test');

-- --------------------------------------------------------

--
-- 테이블 구조 `TRAVELINFO`
--

CREATE TABLE IF NOT EXISTS `TRAVELINFO` (
  `INFO_UPDATE` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- 테이블의 덤프 데이터 `TRAVELINFO`
--

INSERT INTO `TRAVELINFO` (`INFO_UPDATE`) VALUES
(1),
(2),
(3),
(4),
(5),
(6),
(7),
(8),
(9),
(10),
(11),
(12),
(13);

-- --------------------------------------------------------

--
-- 테이블 구조 `USER_CATEGORY`
--

CREATE TABLE IF NOT EXISTS `USER_CATEGORY` (
  `USER_CNUM` int(11) NOT NULL,
  `USER_CNAME` varchar(40) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- 테이블의 덤프 데이터 `USER_CATEGORY`
--

INSERT INTO `USER_CATEGORY` (`USER_CNUM`, `USER_CNAME`) VALUES
(1, '관광객'),
(2, '가이드');
