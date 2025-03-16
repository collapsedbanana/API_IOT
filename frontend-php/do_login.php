<?php
session_start();
header("Content-Type: application/json");

// Récupération des données POST
$data = json_decode(file_get_contents("php://input"), true);

// Vérification
if (!isset($data['username'], $data['password'])) {
    http_response_code(400);
    echo json_encode(["error" => "Données invalides"]);
    exit();
}

$username = $data['username'];
$password = $data['password'];

// 1. Appel à /api/auth/login pour récupérer le token JWT
$ch = curl_init("http://192.168.11.70:8080/api/auth/login");
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode(["username" => $username, "password" => $password]));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

// Vérifie que l'API Spring a répondu correctement
if ($http_code !== 200 || !$response) {
    http_response_code(401);
    echo json_encode(["error" => "Échec de l'authentification (API)"]);
    exit();
}

$responseData = json_decode($response, true);
$token = $responseData['token'] ?? null;
$role  = $responseData['role'] ?? null;

if (!$token || !$role) {
    http_response_code(401);
    echo json_encode(["error" => "Token ou rôle manquant dans la réponse"]);
    exit();
}

// Enregistrement en session PHP
$_SESSION['token'] = $token;
$_SESSION['user_id'] = $username;
$_SESSION['role'] = $role;

// Réponse OK
http_response_code(200);
echo json_encode(["message" => "Session créée", "token" => $token, "role" => $role]);
?>
