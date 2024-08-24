<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-type: application/json; charset=utf-8");

$talep_id = isset($_POST["talep_id"]) ? $_POST["talep_id"] : '';

if (empty($talep_id)) {
    echo json_encode(['error' => 'Missing or invalid talep_id']);
    exit;
}

include("config.php");

$sql = "SELECT * FROM yorumlar WHERE yorum_gid = ?";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    die("Prepare failed: " . $conn->error);
}

$stmt->bind_param("i", $talep_id);
$stmt->execute();

$resultArray = array();
$stmt->bind_result($yorum_id, $yorum_gid, $yorum_yazisi, $yorum_kullanici, $yorum_tarihi);

while ($stmt->fetch()) {
    $resultArray[] = array(
        'yorum_id' => $yorum_id,
        'yorum_gid' => $yorum_gid,
        'yorum_yazisi' => $yorum_yazisi,
        'yorum_kullanici' => $yorum_kullanici,
        'yorum_tarihi' => $yorum_tarihi
    );
}

echo json_encode($resultArray);

$stmt->close();
$conn->close();
?>
