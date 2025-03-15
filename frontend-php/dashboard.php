<?php
session_start();

// Vérifier que l'utilisateur est connecté et a un rôle
if (!isset($_SESSION['user_id']) || !isset($_SESSION['password']) || !isset($_SESSION['role'])) {
    header("Location: login.php");
    exit();
}

$httpAuthUser = $_SESSION['user_id'];
$httpAuthPass = $_SESSION['password'];

// Choix de l'endpoint : admin voit tout, les autres voient seulement leurs données
$endpoint = ($_SESSION['role'] === 'ADMIN')
    ? "/api/measurements/all"
    : "/api/measurements/mine";

$api_url = "http://192.168.11.70:8080" . $endpoint;

$opts = [
    "http" => [
        "method" => "GET",
        "header" => "Authorization: Basic " . base64_encode("$httpAuthUser:$httpAuthPass") . "\r\n" .
                    "Content-Type: application/json\r\n"
    ]
];
$context = stream_context_create($opts);

$response = @file_get_contents($api_url, false, $context);
if ($response === false) {
    die("Erreur lors de la récupération des données de l'API (peut-être 401, 403, ou 500).");
}

// Décodage JSON
$data = json_decode($response, true);
if (!is_array($data)) {
    echo "<pre>⚠️ Réponse brute non exploitable :\n$response</pre>";
    $data = []; // Évite les erreurs en cas de mauvaise réponse
}

// Limiter aux 10 dernières mesures
$data = array_slice($data, -10);
$latest = end($data);

// Valeurs nulles par défaut si aucune mesure
if (!$latest) {
    $latest = [
        'temperature' => null,
        'humidity' => null,
        'humiditeSolRaw' => null,
        'luminositeRaw' => null
    ];
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Dashboard - Tableau de Bord</title>
  <link rel="stylesheet" href="css/style.css">
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
  <header>
    <h1>Tableau de Bord</h1>
    <nav>
      <a href="dashboard.php">Dashboard</a> |
      <?php if ($_SESSION['role'] === 'ADMIN'): ?>
        <a href="admin_users.php">Gestion des utilisateurs</a> |
      <?php endif; ?>
      <a href="logout.php">Déconnexion</a>
    </nav>
  </header>

  <main>
    <h2>Dernières mesures</h2>
    <div class="cards-container">
      <div class="card">
        <h3>Température</h3>
        <p>
          <?php 
          echo isset($latest['temperature']) && $latest['temperature'] !== null 
               ? $latest['temperature'] . " °C" 
               : '--'; 
          ?>
        </p>
      </div>
      <div class="card">
        <h3>Humidité</h3>
        <p>
          <?php 
          echo isset($latest['humidity']) && $latest['humidity'] !== null
               ? $latest['humidity'] . " %"
               : '--';
          ?>
        </p>
      </div>
      <div class="card">
        <h3>Humidité du Sol</h3>
        <p>
          <?php 
          echo isset($latest['humiditeSolRaw']) && $latest['humiditeSolRaw'] !== null
               ? $latest['humiditeSolRaw']
               : '--';
          ?>
        </p>
      </div>
      <div class="card">
        <h3>Luminosité</h3>
        <p>
          <?php 
          echo isset($latest['luminositeRaw']) && $latest['luminositeRaw'] !== null
               ? $latest['luminositeRaw']
               : '--';
          ?>
        </p>
      </div>
    </div>

    <h2>Graphique des mesures</h2>
    <canvas id="myChart"></canvas>
  </main>

  <script>
    const data = <?php echo json_encode($data); ?>;
    
    if (!Array.isArray(data) || data.length === 0) {
      console.log("Aucune mesure disponible.");
    } else {
      const labels = data.map(item => item.timestamp);
      const tempData = data.map(item => item.temperature);
      const humData = data.map(item => item.humidity);
      const soilData = data.map(item => item.humiditeSolRaw);
      const lumiData = data.map(item => item.luminositeRaw);

      new Chart(document.getElementById('myChart').getContext('2d'), {
        type: 'line',
        data: {
          labels,
          datasets: [
            {
              label: 'Température (°C)',
              data: tempData,
              borderColor: 'red',
              fill: false
            },
            {
              label: 'Humidité (%)',
              data: humData,
              borderColor: 'blue',
              fill: false
            },
            {
              label: 'Humidité du Sol',
              data: soilData,
              borderColor: 'orange',
              fill: false
            },
            {
              label: 'Luminosité',
              data: lumiData,
              borderColor: 'green',
              fill: false
            }
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
