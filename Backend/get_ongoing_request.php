<?php
error_reporting(0);
header("Content-type: application/json; charset=utf-8");

include("config.php");

$talep_id = $_POST["talep_id"];

$sql = "SELECT * FROM talepler WHERE talep_durumu = 'Devam Ediyor' AND talep_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $talep_id);
$stmt->execute();

$resultArray = array();
$stmt->bind_result($talep_id, $talep_konu, $talep_aciklama, $talep_muhtar, $talep_ilce, $talep_mahalle, $talep_olusturan, $talep_turu, $kisi_tur, $talep_durumu, $talep_sonucu, $talep_tarihi, $talep_tamamlanma_tarihi);

if ($stmt->fetch()) {
    $resultArray = array(
        'talep_id' => $talep_id,
        'talep_konu' => $talep_konu,
        'talep_aciklama' => $talep_aciklama,
        'talep_muhtar' => $talep_muhtar,
        'talep_ilce' => $talep_ilce,
        'talep_mahalle' => $talep_mahalle,
        'talep_olusturan' => $talep_olusturan,
        'talep_turu' => $talep_turu,
        'kisi_tur' => $kisi_tur,
        'talep_durumu' => $talep_durumu,
        'talep_sonucu' => $talep_sonucu,
        'talep_tarihi' => $talep_tarihi,
        'talep_tamamlanma_tarihi' => $talep_tamamlanma_tarihi
    );
}

echo json_encode($resultArray);

$stmt->close();
$conn->close();
?>
