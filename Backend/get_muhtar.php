<?php
error_reporting(0);
header("Content-type: application/json; charset=utf-8");

include("config.php");

$muhtar_adisoyadi = $_POST["muhtar_adisoyadi"];

$sql = "SELECT * FROM muhtarlar WHERE muhtar_adisoyadi = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $muhtar_adisoyadi);
$stmt->execute();

$resultArray = array();
$stmt->bind_result($muhtar_id, $muhtar_adisoyadi, $muhtar_telefon, $muhtar_aciklama, $muhtar_ilce, $muhtar_mahalle, $muhtar_mi);

if ($stmt->fetch()) {
    $resultArray = array(
        'muhtar_id' => $muhtar_id,
        'muhtar_adisoyadi' => $muhtar_adisoyadi,
        'muhtar_telefon' => $muhtar_telefon,
        'muhtar_aciklama' => $muhtar_aciklama,
        'muhtar_ilce' => $muhtar_ilce,
        'muhtar_mahalle' => $muhtar_mahalle,
        'muhtar_mi' => $muhtar_mi,
    );
}

echo json_encode($resultArray);

$stmt->close();
$conn->close();
?>
