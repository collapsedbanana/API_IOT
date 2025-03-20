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
    document.getElementById('loginForm').addEventListener('submit', async function(e) {
      e.preventDefault();
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;

      try {
        const authResp = await fetch("http://192.168.11.70:8080/api/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password })
        });

        if (!authResp.ok) throw new Error("Échec de l'authentification");
        const authData = await authResp.json();

        const sessionResp = await fetch("do_login.php", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username,
            token: authData.token,
            role: authData.role
          })
        });

        if (!sessionResp.ok) throw new Error("Erreur session PHP");
        window.location.href = "dashboard.php";
      } catch (err) {
        document.getElementById("errorMsg").innerText = err.message;
      }
    });
  </script>
</body>
</html>
