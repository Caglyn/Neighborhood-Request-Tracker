<?php
header("Content-Type: application/json");

// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

include("config.php");

// Get POST data
$talep_id = isset($_POST['talep_id']) ? $_POST['talep_id'] : null;
$talep_durumu = isset($_POST['talep_durumu']) ? $_POST['talep_durumu'] : null;
$talep_sonucu = isset($_POST['talep_sonucu']) ? $_POST['talep_sonucu'] : null;
$talep_tamamlanma_tarihi = isset($_POST['talep_tamamlanma_tarihi']) ? $_POST['talep_tamamlanma_tarihi'] : null;

if ($talep_id && $talep_durumu && $talep_sonucu) {
    // Prepare the SQL statement
    $stmt = $conn->prepare("UPDATE talepler SET talep_durumu = ?, talep_sonucu = ?, talep_tamamlanma_tarihi = ? WHERE talep_id = ?");
    if (!$stmt) {
        die(json_encode(array("status" => "error", "message" => "Prepare failed: " . $conn->error)));
    }

    $stmt->bind_param("sssi", $talep_durumu, $talep_sonucu, $talep_tamamlanma_tarihi, $talep_id);

    if ($stmt->execute()) {
        $response = array("status" => "success");
    } else {
        $response = array("status" => "error", "message" => $stmt->error);
    }

    $stmt->close();
} else {
    $response = array("status" => "error", "message" => "Missing parameters");
}

$conn->close();

// Return JSON response
echo json_encode($response);
?>
