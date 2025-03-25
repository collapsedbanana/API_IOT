<?php
session_start();
if (!isset($_SESSION['token'])) {
    header("Location: login.php");
    exit();
}
$token = htmlspecialchars($_SESSION['token'], ENT_QUOTES, 'UTF-8');
?>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <title>Historique des mesures</title>
  <link rel="stylesheet" href="assets/css/styles.css" />
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <style>
    body { font-family: Arial; margin: 20px; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { padding: 10px; border: 1px solid #ccc; text-align: center; }
    th { background-color: #f0f0f0; }
  </style>
</head>
<body>

  <header>
    <h1>📈 Historique des mesures</h1>
    <nav>
      <a href="dashboard.php">Retour au tableau de bord</a>
      <a href="logout.php" class="logout">Déconnexion</a>
    </nav>
  </header>

  <main>
    <h2>Tableau des dernières mesures</h2>
    <table id="historyTable">
      <thead>
        <tr>
          <th>Date / Heure</th>
          <th>Température (°C)</th>
          <th>Humidité (%)</th>
          <th>Humidité Sol</th>
          <th>Luminosité</th>
        </tr>
      </thead>
      <tbody></tbody>
    </table>

    <h2>Graphique</h2>
    <canvas id="historyChart"></canvas>
  </main>

  <script>
    const token = "<?= $token ?>";
    const apiUrl = "http://192.168.11.70:8080/api/measurements/mine";

    async function fetchMeasurements() {
      try {
        const resp = await fetch(apiUrl, {
          headers: {
            "Authorization": "Bearer " + token
          }
        });

        if (!resp.ok) throw new Error("Erreur lors du chargement des mesures");
        const data = await resp.json();

        const tbody = document.querySelector('#historyTable tbody');
        tbody.innerHTML = '';

        const labels = [];
        const tempData = [];
        const humData = [];
        const soilData = [];
        const lumiData = [];

        data.forEach(item => {
          labels.push(item.timestamp);
          tempData.push(item.temperature);
          humData.push(item.humidity);
          soilData.push(item.humiditeSolRaw);
          lumiData.push(item.luminositeRaw);

          tbody.innerHTML += `
            <tr>
              <td>${item.timestamp}</td>
              <td>${item.temperature ?? '--'}</td>
              <td>${item.humidity ?? '--'}</td>
              <td>${item.humiditeSolRaw ?? '--'}</td>
              <td>${item.luminositeRaw ?? '--'}</td>
            </tr>
          `;
        });

        new Chart(document.getElementById("historyChart").getContext("2d"), {
          type: 'line',
          data: {
            labels,
            datasets: [
              { label: "Température", data: tempData, borderColor: "red", fill: false },
              { label: "Humidité", data: humData, borderColor: "blue", fill: false },
              { label: "Humidité du sol", data: soilData, borderColor: "green", fill: false },
              { label: "Luminosité", data: lumiData, borderColor: "orange", fill: false },
            ]
          },
          options: {
            responsive: true,
            scales: {
              x: { title: { display: true, text: "Horodatage" } },
              y: { title: { display: true, text: "Valeurs" } }
            }
          }
        });

      } catch (err) {
        alert("❌ Erreur : " + err.message);
      }
    }

    fetchMeasurements();
  </script>
</body>
</html>
