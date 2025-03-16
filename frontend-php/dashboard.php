<?php 
session_start();

// Vérifier que l'utilisateur est connecté et a un rôle
if (!isset($_SESSION['token']) || !isset($_SESSION['role'])) {
    header("Location: login.php");
    exit();
}

// Choix de l'endpoint : ADMIN voit tout, les autres voient seulement leurs données
$endpoint = ($_SESSION['role'] === 'ADMIN')
    ? "/api/measurements/all"
    : "/api/measurements/mine";

$api_url = "http://192.168.11.70:8080" . $endpoint;

// Utiliser cURL pour l'appel API avec le token JWT
$ch = curl_init($api_url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    "Authorization: Bearer " . $_SESSION['token'],
    "Content-Type: application/json"
]);
$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($http_code !== 200) {
    http_response_code($http_code);
    die("Erreur API : Code HTTP $http_code (401 = non autorisé, 500 = erreur serveur, etc)");
}

// Décodage JSON
$data = json_decode($response, true);
if (!is_array($data)) {
    echo "<pre>⚠️ Réponse brute non exploitable :\n$response</pre>";
    $data = []; // Évite les erreurs
}

// Limiter aux 10 dernières mesures
$data = array_slice($data, -10);
$latest = end($data);

// Valeurs nulles par défaut si aucune mesure
if (!$latest) {
    $latest = [
        'temperature'     => null,
        'humidity'        => null,
        'humiditeSolRaw'  => null,
        'luminositeRaw'   => null
    ];
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Dashboard - Tableau de Bord</title>
  <link rel="stylesheet" href="css/style.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
  <header>
    <h1>Tableau de Bord</h1>
    <nav>
      <div class="nav-links">
        <?php if ($_SESSION['role'] === 'ADMIN'): ?>
          <a href="admin_users.php">Gestion des utilisateurs</a>
        <?php endif; ?>
      </div>
      <a class="logout" href="logout.php">Déconnexion</a>
    </nav>
  </header>

  <main>
    <h2>Dernières mesures</h2>
    <div class="cards-container">
      <div class="card card-temperature">
        <i class="fas fa-thermometer-half"></i>
        <h3>Température</h3>
        <p><?= $latest['temperature'] !== null ? $latest['temperature'] . " °C" : '--'; ?></p>
      </div>
      <div class="card card-humidity">
        <i class="fas fa-tint"></i>
        <h3>Humidité</h3>
        <p><?= $latest['humidity'] !== null ? $latest['humidity'] . " %" : '--'; ?></p>
      </div>
      <div class="card card-soil">
        <i class="fas fa-leaf"></i>
        <h3>Humidité du Sol</h3>
        <p><?= $latest['humiditeSolRaw'] !== null ? $latest['humiditeSolRaw'] : '--'; ?></p>
      </div>
      <div class="card card-luminosity">
        <i class="fas fa-sun"></i>
        <h3>Luminosité</h3>
        <p><?= $latest['luminositeRaw'] !== null ? $latest['luminositeRaw'] : '--'; ?></p>
      </div>
    </div>

    <h2>Graphique des mesures</h2>
    <canvas id="myChart"></canvas>
  </main>

  <script>
    const data = <?= json_encode($data); ?>;
    if (Array.isArray(data) && data.length > 0) {
      const labels   = data.map(item => item.timestamp);
      const tempData = data.map(item => item.temperature);
      const humData  = data.map(item => item.humidity);
      const soilData = data.map(item => item.humiditeSolRaw);
      const lumiData = data.map(item => item.luminositeRaw);

      new Chart(document.getElementById('myChart').getContext('2d'), {
        type: 'line',
        data: {
          labels,
          datasets: [
            { label: 'Température (°C)', data: tempData, borderColor: 'red', fill: false },
            { label: 'Humidité (%)', data: humData, borderColor: 'blue', fill: false },
            { label: 'Humidité du Sol', data: soilData, borderColor: 'orange', fill: false },
            { label: 'Luminosité', data: lumiData, borderColor: 'green', fill: false }
          ]
        },
        options: {
          responsive: true,
          scales: {
            x: { title: { display: true, text: 'Horodatage' } },
            y: { title: { display: true, text: 'Valeurs' } }
          }
        }
      });
    }
  </script>
</body>
</html>
