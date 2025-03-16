<?php
session_start();
if (isset($_SESSION['user_id'])) {
    header("Location: dashboard.php");
    exit();
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Connexion</title>
  <style>
    body { font-family: Arial; display: flex; height: 100vh; justify-content: center; align-items: center; background: #f4f4f4; }
    .login-container { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px #ccc; width: 300px; }
    .login-container h2 { text-align: center; }
    .login-container input, button { width: 100%; padding: 10px; margin: 8px 0; border-radius: 4px; border: 1px solid #ccc; }
    button { background: #007BFF; color: white; cursor: pointer; }
    .error { color: red; text-align: center; }
  </style>
</head>
<body>
  <div class="login-container">
    <h2>Connexion</h2>
    <form id="loginForm">
      <input type="text" id="username" placeholder="Nom d'utilisateur" required>
      <input type="password" id="password" placeholder="Mot de passe" required>
      <button type="submit">Se connecter</button>
    </form>
    <div id="errorMsg" class="error"></div>
  </div>

  <script>
    document.getElementById('loginForm').addEventListener('submit', function(e) {
      e.preventDefault();
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;

      // 1. Authentifie via Spring API
      fetch("http://192.168.11.70:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      })
      .then(resp => {
        if (!resp.ok) throw new Error("Échec de l'authentification");
        return resp.json();
      })
      .then(data => {
        // 2. Crée la session PHP avec les données Spring
        return fetch("do_login.php", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username: data.username,
            token: data.token,
            role: data.role
          })
        });
      })
      .then(resp => {
        if (!resp.ok) throw new Error("Erreur lors de la création de la session");
        window.location.href = "dashboard.php";
      })
      .catch(err => {
        document.getElementById("errorMsg").innerText = err.message;
      });
    });
  </script>
</body>
</html>
