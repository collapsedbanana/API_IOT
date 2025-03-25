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
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />

  <title>Connexion</title>

  <!-- RemixIcons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/remixicon/3.5.0/remixicon.css" crossorigin="" />

  <!-- Feuille de style -->
  <link rel="stylesheet" href="assets/css/styles.css" />
</head>
<body>
  <div class="login">
    <img src="assets/img/login-bg.png" alt="Image de fond" class="login__bg" />

    <form class="login__form" id="loginForm">
      <h1 class="login__title">Connexion</h1>

      <div class="login__inputs">
        <div class="login__box">
          <input type="text" id="username" placeholder="Nom d'utilisateur" required class="login__input" />
          <i class="ri-mail-fill"></i>
        </div>

        <div class="login__box">
          <input type="password" id="password" placeholder="Mot de passe" required class="login__input" />
          <i class="ri-lock-2-fill"></i>
        </div>
      </div>

      <div class="login__check">
        <div class="login__check-box">
          <input type="checkbox" class="login__check-input" id="user-check" />
          <label for="user-check" class="login__check-label">Se souvenir de moi</label>
        </div>

        <a href="#" class="login__forgot">Mot de passe oublié ?</a>
      </div>

      <button type="submit" class="login__button">Se connecter</button>

      <div class="login__register">
      Pas encore de compte ? <a href="register.php">Créer un compte</a>
      </div>

      <div id="errorMsg" style="color:red; text-align:center; margin-top: 0.5rem;"></div>
    </form>
  </div>

  <script>
    document.getElementById('loginForm').addEventListener('submit', async function(e) {
      e.preventDefault();
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;

      try {
        const authResp = await fetch("http://192.168.10.70:8080/api/auth/login", {
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

        if (!sessionResp.ok) throw new Error("Erreur lors de la création de session");
        window.location.href = "dashboard.php";
      } catch (err) {
        document.getElementById("errorMsg").innerText = err.message;
      }
    });
  </script>
</body>
</html>
