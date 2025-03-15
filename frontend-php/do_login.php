<?php
session_start();

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['username'], $data['password'])) {
    http_response_code(400);
    echo json_encode(["error" => "Données invalides"]);
    exit();
}

$_SESSION['user_id'] = $data['username'];
$_SESSION['password'] = $data['password'];

$authHeader = "Authorization: Basic " . base64_encode($_SESSION['user_id'] . ":" . $_SESSION['password']);
$opts = [
    "http" => [
        "method" => "GET",
        "header" => "$authHeader\r\nContent-Type: application/json\r\n"
    ]
];
$ctx = stream_context_create($opts);
$response = @file_get_contents("http://192.168.11.70:8080/api/auth/me", false, $ctx);
$info = json_decode($response, true);

if (!$info || !isset($info['role'])) {
    http_response_code(401);
    echo json_encode(["error" => "Impossible de récupérer le rôle"]);
    exit();
}

$_SESSION['role'] = $info['role'];

http_response_code(200);
echo json_encode(["message" => "Session créée", "role" => $info['role']]);
?>
