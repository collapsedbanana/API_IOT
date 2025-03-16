<?php
session_start();
header("Content-Type: application/json");

// Lecture des données
$data = json_decode(file_get_contents("php://input"), true);

$username = $data['username'] ?? null;
$token    = $data['token'] ?? null;
$role     = $data['role'] ?? null;

if (!$username || !$token || !$role) {
    http_response_code(400);
    echo json_encode(["error" => "Données manquantes pour la session"]);
    exit();
}

// Appel à /api/auth/me pour valider le token côté Spring
$opts = [
    "http" => [
        "method" => "GET",
        "header" => "Authorization: Bearer $token\r\nContent-Type: application/json\r\n"
    ]
];
$ctx = stream_context_create($opts);
$response = @file_get_contents("http://192.168.11.70:8080/api/auth/me", false, $ctx);
$info = json_decode($response, true);

if (!$info || !isset($info['username'])) {
    http_response_code(401);
    echo json_encode(["error" => "Token invalide ou expiré"]);
    exit();
}

// Création de session PHP
$_SESSION['user_id'] = $username;
$_SESSION['role'] = $role;
$_SESSION['token'] = $token;

http_response_code(200);
echo json_encode(["message" => "Session créée", "role" => $role]);
?>
