<?php
session_start();
header("Content-Type: application/json");

// Récupère les données envoyées par le frontend
$data = json_decode(file_get_contents("php://input"), true);
$token = $data['token'] ?? null;

if (!$token) {
    http_response_code(400);
    echo json_encode(["error" => "Token manquant"]);
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
$response = @file_get_contents("http://192.168.11.70:8080/api/auth/me", false, $ctx);
$info = json_decode($response, true);

if (!$info || !isset($info['username']) || !isset($info['role'])) {
    http_response_code(401);
    echo json_encode(["error" => "Token invalide ou expiré"]);
    exit();
}

// ✅ Enregistrement de la session à partir des données du backend
$_SESSION['user_id'] = $info['username'];
$_SESSION['role'] = $info['role'];
$_SESSION['token'] = $token;

http_response_code(200);
echo json_encode(["message" => "Session créée", "role" => $info['role']]);
