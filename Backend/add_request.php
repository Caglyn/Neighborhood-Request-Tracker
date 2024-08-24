<?php
error_reporting(0);
header("Content-type: application/json; charset=utf-8");

include("config.php");

$talep_konu = $_POST['talep_konu'];
$talep_aciklama = $_POST['talep_aciklama'];
$talep_muhtar = $_POST['talep_muhtar'];
$talep_ilce = $_POST['talep_ilce'];
$talep_mahalle = $_POST['talep_mahalle'];
$talep_olusturan = $_POST['talep_olusturan'];
$talep_turu = $_POST['talep_turu'];
$kisi_tur = $_POST['kisi_tur'];
$talep_durumu = $_POST['talep_durumu'];
$talep_sonucu = $_POST['talep_sonucu'];
$talep_tarihi = $_POST['talep_tarihi'];
$talep_tamamlanma_tarihi = $_POST['talep_tamamlanma_tarihi'];

$sql = "INSERT INTO talepler (talep_konu, talep_aciklama, talep_muhtar, talep_ilce, talep_mahalle, talep_olusturan, talep_turu, kisi_tur, talep_durumu, talep_sonucu, talep_tarihi, talep_tamamlanma_tarihi) 
        VALUES ('$talep_konu', '$talep_aciklama', '$talep_muhtar', '$talep_ilce', '$talep_mahalle', '$talep_olusturan', '$talep_turu', '$kisi_tur', '$talep_durumu', '$talep_sonucu', '$talep_tarihi', '$talep_tamamlanma_tarihi')";

if ($conn->query($sql) === TRUE) {
    echo json_encode(array("status" => "success"));
} else {
    echo json_encode(array("status" => "error", "message" => $conn->error));
}

$conn->close();
?>