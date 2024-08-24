<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
header("Content-type: application/json; charset=utf-8");

include("config.php");

if (!isset($_POST["kullanici_adi"]) || !isset($_POST['kullanici_sifre'])) {
    echo json_encode(array("error" => "Missing parameters"));
    exit();
}

$kullanici_adi = $_POST["kullanici_adi"];
$kullanici_sifre = $_POST['kullanici_sifre'];

$sql = "SELECT * FROM kullanicilar WHERE kullanici_adi = ? AND kullanici_sifre = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $kullanici_adi, $kullanici_sifre);
$stmt->execute();

$resultArray = array();
$stmt->bind_result($kullanici_id, $kullanici_adisoyadi, $kullanici_adi_db, $kullanici_sifre_db, $kullanici_yetki);

if ($stmt->fetch()) {
    $resultArray = array(
        'kullanici_id' => $kullanici_id,
        'kullanici_adisoyadi' => $kullanici_adisoyadi,
        'kullanici_adi' => $kullanici_adi_db,
        'kullanici_yetki' => $kullanici_yetki
    );
} else {
    echo json_encode(array("error" => "İsim veya şifre yanlış!"));
    $connection->close();
    exit();
}

echo json_encode($resultArray);

$conn->close();
?>
