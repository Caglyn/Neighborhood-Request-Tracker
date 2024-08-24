<?php
error_reporting(0);
header("Content-type: application/json; charset=utf-8");

include("config.php");

$sql = "SELECT * FROM talep_turu ORDER BY tur_adi ASC";
$result = $conn->query($sql);

$resultArray = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $satirlar = array(
            'tur_id' => $row['tur_id'],
            'tur_adi' => $row['tur_adi'],
         
        );
        array_push($resultArray, $satirlar);
    }
}

echo json_encode($resultArray);

$conn->close();
?>
