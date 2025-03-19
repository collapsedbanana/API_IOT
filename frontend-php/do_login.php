<?php
session_start();
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);
$username = $data['username'] ?? null;
$token    = $data['token'] ?? null;
$role     = $data['role'] ?? null;

if (!$username || !$token || !$role) {
    http_response_code(400);
    echo json_encode(["error" => "Données manquantes pour la session"]);
    exit();
}

// Vérifie la validité du token via Spring
$opts = [
    "http" => [
        "method" => "GET",
        "header" => "Authorization: Bearer $token\r\nContent-Type: application/json\r\n"
    ]
];
$ctx = stream_context_create($opts);
$response = @file_get_contents("http://192.168.11.70:8443/api/auth/me", false, $ctx);
$info = json_decode($response, true);

if (!$info || !isset($info['username'])) {
    http_response_code(401);
    echo json_encode(["error" => "Token invalide ou expiré"]);
    exit();
}

// Enregistrement de la session
$_SESSION['user_id'] = $username;
$_SESSION['role'] = $role;
$_SESSION['token'] = $token;

http_response_code(200);
echo json_encode(["message" => "Session créée", "role" => $role]);
