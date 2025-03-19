<?php
session_start();
if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'ADMIN') {
    header("Location: login.php");
    exit();
}
$token = htmlspecialchars($_SESSION['token'], ENT_QUOTES, 'UTF-8');
?>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Gestion des Utilisateurs</title>
  <style>
    body { font-family: Arial; margin: 20px; }
    table { border-collapse: collapse; width: 100%; margin-top: 20px; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
    th { background-color: #f4f4f4; }
    input, select, button { padding: 5px; }
    .danger { background-color: red; color: white; }
    .navbar {
      background-color: #333; padding: 10px;
      display: flex; justify-content: space-between;
    }
    .navbar a {
      color: white; margin: 0 10px; text-decoration: none;
    }
  </style>
</head>
<body>

<div class="navbar">
  <div>
    <a href="dashboard.php">🏠 Menu principal</a>
    <a href="admin_users.php">👥 Gestion des utilisateurs</a>
  </div>
  <a href="logout.php">🚪 Déconnexion</a>
</div>

<h1>Gestion des utilisateurs</h1>

<h2>Créer un nouvel utilisateur</h2>
<form id="createUserForm">
  <input type="text" id="newUsername" placeholder="Nom d'utilisateur" required>
  <input type="password" id="newPassword" placeholder="Mot de passe" required>
  <button type="submit">Créer</button>
</form>

<h2>Liste des utilisateurs</h2>
<table id="usersTable">
  <thead>
    <tr>
      <th>Nom</th>
      <th>Rôle</th>
      <th>Capteur Assigné</th>
      <th>Température</th>
      <th>Humidité</th>
      <th>Luminosité</th>
      <th>Humidité Sol</th>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody></tbody>
</table>

<script>
const apiBase = 'http://192.168.11.70:8443';
const authHeader = {
  'Authorization': 'Bearer <?= $token ?>',
  'Content-Type': 'application/json'
};

function handle401(resp) {
  if (resp.status === 401) {
    alert("⚠️ Session expirée, veuillez vous reconnecter.");
    window.location.href = "login.php";
    return true;
  }
  return false;
}

let devices = [];

async function loadDevices() {
  try {
    const resp = await fetch(`${apiBase}/api/devices/all`, { headers: authHeader });
    if (handle401(resp)) return;
    if (!resp.ok) throw new Error("Erreur HTTP lors du chargement des capteurs");
    devices = await resp.json();
  } catch (err) {
    alert("❌ Impossible de charger les capteurs !");
    console.error("Erreur devices:", err);
  }
}

async function loadUsers() {
  try {
    const resp = await fetch(`${apiBase}/api/admin/users/all`, { headers: authHeader });
    if (handle401(resp)) return;
    const users = await resp.json();
    const tbody = document.querySelector('#usersTable tbody');
    tbody.innerHTML = '';

    users.forEach(user => {
      const perm = user.permission || {};
      const assignedDevice = devices.find(d => d.user && d.user.username === user.username);
      const deviceOptions = ['<option value="">-- Aucun --</option>']
        .concat(devices.map(d => {
          const selected = assignedDevice && assignedDevice.deviceId === d.deviceId ? 'selected' : '';
          return `<option value="${d.deviceId}" ${selected}>${d.deviceId}</option>`;
        })).join('');

      tbody.innerHTML += `
        <tr>
          <td>${user.username}</td>
          <td>${user.role}</td>
          <td>
            <select onchange="assignDevice('${user.username}', this.value)">
              ${deviceOptions}
            </select>
          </td>
          <td><input type="checkbox" ${perm.canViewTemperature ? 'checked' : ''} data-user="${user.username}" data-field="canViewTemperature"></td>
          <td><input type="checkbox" ${perm.canViewHumidity ? 'checked' : ''} data-user="${user.username}" data-field="canViewHumidity"></td>
          <td><input type="checkbox" ${perm.canViewLuminosite ? 'checked' : ''} data-user="${user.username}" data-field="canViewLuminosite"></td>
          <td><input type="checkbox" ${perm.canViewHumiditeSol ? 'checked' : ''} data-user="${user.username}" data-field="canViewHumiditeSol"></td>
          <td>
            <button onclick="updatePermissions('${user.username}')">Enregistrer</button>
            <button class="danger" onclick="deleteUser('${user.username}')">Supprimer</button>
          </td>
        </tr>
      `;
    });
  } catch (err) {
    alert("❌ Erreur lors du chargement des utilisateurs.");
    console.error("Erreur loadUsers:", err);
  }
}

function assignDevice(username, deviceId) {
  const url = deviceId
    ? `${apiBase}/api/devices/assign?deviceId=${deviceId}&username=${username}`
    : `${apiBase}/api/devices/unassign?username=${username}`;

  fetch(url, { method: 'POST', headers: authHeader })
    .then(resp => {
      if (handle401(resp)) return;
      if (resp.ok) {
        alert(deviceId ? `✅ Capteur ${deviceId} assigné à ${username}` : `✅ Capteur dissocié`);
        loadUsers();
      } else {
        alert('❌ Erreur assignation');
      }
    });
}

function updatePermissions(username) {
  const inputs = document.querySelectorAll(`[data-user="${username}"]`);
  const data = {};
  inputs.forEach(input => {
    data[input.dataset.field] = input.checked;
  });

  fetch(`${apiBase}/api/admin/users/update-permissions/${username}`, {
    method: 'PUT',
    headers: authHeader,
    body: JSON.stringify(data)
  }).then(resp => {
    if (handle401(resp)) return;
    if (resp.ok) alert('✅ Permissions mises à jour');
    else alert('❌ Erreur mise à jour');
  });
}

function deleteUser(username) {
  if (!confirm(`Supprimer l'utilisateur ${username} ?`)) return;
  fetch(`${apiBase}/api/admin/users/delete/${username}`, {
    method: 'DELETE',
    headers: authHeader
  }).then(resp => {
    if (handle401(resp)) return;
    if (resp.ok) {
      alert('🗑️ Utilisateur supprimé');
      loadUsers();
    } else {
      alert('❌ Erreur suppression');
    }
  });
}

document.getElementById('createUserForm').addEventListener('submit', e => {
  e.preventDefault();
  const username = document.getElementById('newUsername').value;
  const password = document.getElementById('newPassword').value;

  fetch(`${apiBase}/api/admin/users/create`, {
    method: 'POST',
    headers: authHeader,
    body: JSON.stringify({ username, password })
  }).then(resp => {
    if (handle401(resp)) return;
    if (resp.ok) {
      alert('✅ Utilisateur créé');
      loadUsers();
    } else {
      alert('❌ Erreur création utilisateur');
    }
  });
});

// Chargement initial
(async function init() {
  await loadDevices();
  await loadUsers();
})();
</script>
</body>
</html>
