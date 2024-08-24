-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 24, 2024 at 09:30 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `neighborhood_request_tracker`
--

-- --------------------------------------------------------

--
-- Table structure for table `ilceler`
--

CREATE TABLE `ilceler` (
  `ilce_id` int(11) NOT NULL,
  `ilce_adi` varchar(205) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `ilceler`
--

INSERT INTO `ilceler` (`ilce_id`, `ilce_adi`) VALUES
(20, 'example_district');

-- --------------------------------------------------------

--
-- Table structure for table `kullanicilar`
--

CREATE TABLE `kullanicilar` (
  `kullanici_id` int(11) NOT NULL,
  `kullanici_adisoyadi` varchar(70) NOT NULL,
  `kullanici_adi` varchar(70) NOT NULL,
  `kullanici_sifre` float NOT NULL,
  `kullanici_yetki` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `kullanicilar`
--

INSERT INTO `kullanicilar` (`kullanici_id`, `kullanici_adisoyadi`, `kullanici_adi`, `kullanici_sifre`, `kullanici_yetki`) VALUES
(6, 'name-surname', 'username', 123, 0);

-- --------------------------------------------------------

--
-- Table structure for table `mahalleler`
--

CREATE TABLE `mahalleler` (
  `mahalle_id` int(11) NOT NULL,
  `mahalle_gid` int(11) DEFAULT NULL,
  `mahalle_adi` varchar(19) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `mahalleler`
--

INSERT INTO `mahalleler` (`mahalle_id`, `mahalle_gid`, `mahalle_adi`) VALUES
(775, 1, 'example_neighborhoo');

-- --------------------------------------------------------

--
-- Table structure for table `muhtarlar`
--

CREATE TABLE `muhtarlar` (
  `muhtar_id` int(11) NOT NULL,
  `muhtar_adisoyadi` varchar(250) NOT NULL,
  `muhtar_telefon` varchar(250) NOT NULL,
  `muhtar_aciklama` varchar(250) NOT NULL,
  `muhtar_ilce` int(11) NOT NULL,
  `muhtar_mahalle` int(11) NOT NULL,
  `muhtar_mi` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Dumping data for table `muhtarlar`
--

INSERT INTO `muhtarlar` (`muhtar_id`, `muhtar_adisoyadi`, `muhtar_telefon`, `muhtar_aciklama`, `muhtar_ilce`, `muhtar_mahalle`, `muhtar_mi`) VALUES
(32475, 'name-surname', '1111111111', '', 20, 775, 0);

-- --------------------------------------------------------

--
-- Table structure for table `talepler`
--

CREATE TABLE `talepler` (
  `talep_id` int(11) NOT NULL,
  `talep_konu` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `talep_aciklama` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `talep_muhtar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci NOT NULL,
  `talep_ilce` text CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci NOT NULL,
  `talep_mahalle` text CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci NOT NULL,
  `talep_olusturan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci NOT NULL,
  `talep_turu` text CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci NOT NULL,
  `kisi_tur` text CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci NOT NULL,
  `talep_durumu` text CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci NOT NULL,
  `talep_sonucu` text CHARACTER SET utf8mb4 COLLATE utf8mb4_turkish_ci NOT NULL,
  `talep_tarihi` date NOT NULL,
  `talep_tamamlanma_tarihi` date NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `talepler`
--

INSERT INTO `talepler` (`talep_id`, `talep_konu`, `talep_aciklama`, `talep_muhtar`, `talep_ilce`, `talep_mahalle`, `talep_olusturan`, `talep_turu`, `kisi_tur`, `talep_durumu`, `talep_sonucu`, `talep_tarihi`, `talep_tamamlanma_tarihi`) VALUES
(285, 'title', 'description', ' mukhtar', 'district', 'neighborhood', 'user', 'request type', 'MUHTAR', 'Devam Ediyor', '', '2024-08-24', '0000-00-00'),
(286, 'title2', 'description2', ' mukhtar2', 'district2', 'neighborhood2', 'user', 'request type2', 'MUHTAR', 'Tamamlandı', 'OLUMLU', '2024-08-24', '2024-08-24');

-- --------------------------------------------------------

--
-- Table structure for table `talep_turu`
--

CREATE TABLE `talep_turu` (
  `tur_id` int(11) NOT NULL,
  `tur_adi` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `talep_turu`
--

INSERT INTO `talep_turu` (`tur_id`, `tur_adi`) VALUES
(39, 'example_request_type');

-- --------------------------------------------------------

--
-- Table structure for table `yorumlar`
--

CREATE TABLE `yorumlar` (
  `yorum_id` int(11) NOT NULL,
  `yorum_gid` int(11) NOT NULL,
  `yorum_yazisi` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `yorum_kullanici` text CHARACTER SET utf8 COLLATE utf8_turkish_ci NOT NULL,
  `yorum_tarihi` datetime NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `yorumlar`
--

INSERT INTO `yorumlar` (`yorum_id`, `yorum_gid`, `yorum_yazisi`, `yorum_kullanici`, `yorum_tarihi`) VALUES
(166, 0, 'comment', 'user', '2020-03-18 09:13:00'),
(167, 0, 'yorum_gid is the id of the request', 'user', '2020-03-18 09:13:00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ilceler`
--
ALTER TABLE `ilceler`
  ADD PRIMARY KEY (`ilce_id`);

--
-- Indexes for table `kullanicilar`
--
ALTER TABLE `kullanicilar`
  ADD PRIMARY KEY (`kullanici_id`);

--
-- Indexes for table `mahalleler`
--
ALTER TABLE `mahalleler`
  ADD PRIMARY KEY (`mahalle_id`);

--
-- Indexes for table `muhtarlar`
--
ALTER TABLE `muhtarlar`
  ADD PRIMARY KEY (`muhtar_id`);

--
-- Indexes for table `talepler`
--
ALTER TABLE `talepler`
  ADD PRIMARY KEY (`talep_id`);

--
-- Indexes for table `talep_turu`
--
ALTER TABLE `talep_turu`
  ADD PRIMARY KEY (`tur_id`);

--
-- Indexes for table `yorumlar`
--
ALTER TABLE `yorumlar`
  ADD PRIMARY KEY (`yorum_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ilceler`
--
ALTER TABLE `ilceler`
  MODIFY `ilce_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `kullanicilar`
--
ALTER TABLE `kullanicilar`
  MODIFY `kullanici_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `mahalleler`
--
ALTER TABLE `mahalleler`
  MODIFY `mahalle_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=776;

--
-- AUTO_INCREMENT for table `muhtarlar`
--
ALTER TABLE `muhtarlar`
  MODIFY `muhtar_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32476;

--
-- AUTO_INCREMENT for table `talepler`
--
ALTER TABLE `talepler`
  MODIFY `talep_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=287;

--
-- AUTO_INCREMENT for table `talep_turu`
--
ALTER TABLE `talep_turu`
  MODIFY `tur_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `yorumlar`
--
ALTER TABLE `yorumlar`
  MODIFY `yorum_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=168;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
