<?php
include("config.php");

if ($conn->connect_error) {
    //Output JSON response for connection error
    $response = array(
        "status" => "error",
        "message" => "Connection failed: " . $conn->connect_error
    );
    echo json_encode($response);
    exit();
}

$yorum_gid = $_POST['yorum_gid'];
$yorum_yazisi = $_POST['yorum_yazisi'];
$yorum_kullanici = $_POST['yorum_kullanici'];
$yorum_tarihi = $_POST['yorum_tarihi'];

//Preparing and executing the SQL query
$sql = "INSERT INTO yorumlar (yorum_gid, yorum_yazisi, yorum_kullanici, yorum_tarihi)
        VALUES (?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("isss", $yorum_gid, $yorum_yazisi, $yorum_kullanici, $yorum_tarihi);

if ($stmt->execute()) {
    $response = array(
        "status" => "success",
        "message" => "Yorum Başarıyla Eklendi"
    );
} else {
    $response = array(
        "status" => "error",
        "message" => "Error: " . $stmt->error
    );
}

$stmt->close();
$conn->close();

echo json_encode($response);
?>
