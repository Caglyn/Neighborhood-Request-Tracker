<?php
error_reporting(0);
header("Content-type: application/json; charset=utf-8");

include("config.php");

$sql = "SELECT * FROM talepler WHERE talep_durumu = 'Devam Ediyor' ORDER BY talep_id DESC";
$result = $conn->query($sql);

$resultArray = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $satirlar = array(
            'talep_id' => $row['talep_id'],
            'talep_konu' => $row['talep_konu'],
            'talep_aciklama' => $row['talep_aciklama'],
            'talep_muhtar' => $row['talep_muhtar'],
            'talep_ilce' => $row['talep_ilce'],
            'talep_mahalle' => $row['talep_mahalle'],
            'talep_olusturan' => $row['talep_olusturan'],
            'talep_turu' => $row['talep_turu'],
            'kisi_tur' => $row['kisi_tur'],
            'talep_durumu' => $row['talep_durumu'],
            'talep_sonucu' => $row['talep_sonucu'],
            'talep_tarihi' => $row['talep_tarihi'],
            'talep_tamamlanma_tarihi' => $row['talep_tamamlanma_tarihi']
        );
        array_push($resultArray, $satirlar);
    }
}

echo json_encode($resultArray);

$conn->close();
?>
