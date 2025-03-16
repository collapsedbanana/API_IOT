<?php
session_start();

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['username'], $data['password'])) {
    http_response_code(400);
    echo json_encode(["error" => "Données invalides"]);
    exit();
}

// 1. Appel à /api/auth/login pour récupérer le token JWT
$ch = curl_init("http://192.168.11.70:8080/api/auth/login");
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($http_code !== 200) {
    http_response_code(401);
    echo json_encode(["error" => "Échec de l'authentification"]);
    exit();
}

$responseData = json_decode($response, true);
$token = $responseData['token'] ?? null;

if (!$token) {
    http_response_code(401);
    echo json_encode(["error" => "Token non reçu"]);
    exit();
}

// 2. Stocke le token dans la session
$_SESSION['token'] = $token;

// 3. Appel à /api/auth/me pour récupérer rôle
$opts = [
    "http" => [
        "method" => "GET",
        "header" => "Authorization: Bearer $token\r\nContent-Type: application/json\r\n"
    ]
];
$ctx = stream_context_create($opts);
$me = file_get_contents("http://192.168.11.70:8080/api/auth/me", false, $ctx);
$info = json_decode($me, true);

if (!$info || !isset($info['role'])) {
    http_response_code(401);
    echo json_encode(["error" => "Impossible de récupérer le rôle"]);
    exit();
}

$_SESSION['user_id'] = $data['username'];
$_SESSION['role'] = $info['role'];

echo json_encode(["message" => "Session créée", "role" => $info['role']]);
?>
