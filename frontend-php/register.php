<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Créer un compte</title>

  <!-- RemixIcons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/remixicon/3.5.0/remixicon.css" crossorigin="" />

  <!-- CSS -->
  <link rel="stylesheet" href="assets/css/styles.css" />
</head>
<body>
  <div class="login">
    <img src="assets/img/login-bg.png" alt="Image de fond" class="login__bg" />

    <form class="login__form" id="registerForm">
      <h1 class="login__title">Inscription</h1>

      <div class="login__inputs">
        <div class="login__box">
          <input type="text" id="username" placeholder="Nom d'utilisateur" required class="login__input" />
          <i class="ri-user-fill"></i>
        </div>

        <div class="login__box">
          <input type="password" id="password" placeholder="Mot de passe" required class="login__input" />
          <i class="ri-lock-2-fill"></i>
        </div>

        <div class="login__box">
          <input type="password" id="confirmPassword" placeholder="Confirmer le mot de passe" required class="login__input" />
          <i class="ri-lock-password-fill"></i>
        </div>
      </div>

      <button type="submit" class="login__button">Créer un compte</button>

      <div class="login__register">
        Déjà inscrit ? <a href="login.php">Connexion</a>
      </div>

      <div id="message" style="text-align:center; margin-top: 0.5rem;"></div>
    </form>
  </div>

  <script>
    document.getElementById('registerForm').addEventListener('submit', async function(e) {
      e.preventDefault();
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;
      const confirmPassword = document.getElementById('confirmPassword').value;
      const messageDiv = document.getElementById('message');

      if (password !== confirmPassword) {
        messageDiv.innerHTML = "<span style='color:red;'>Les mots de passe ne correspondent pas.</span>";
        return;
      }

      try {
        const resp = await fetch("http://192.168.11.70:8080/api/auth/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password })
        });

        if (resp.status === 201) {
          messageDiv.innerHTML = "<span style='color:green;'>✅ Compte créé avec succès !</span>";
        } else if (resp.status === 409) {
          messageDiv.innerHTML = "<span style='color:red;'>⚠️ Ce nom d'utilisateur existe déjà.</span>";
        } else {
          const text = await resp.text();
          messageDiv.innerHTML = `<span style='color:red;'>❌ Erreur : ${text}</span>`;
        }
      } catch (err) {
        messageDiv.innerHTML = `<span style='color:red;'>❌ Erreur serveur : ${err.message}</span>`;
      }
    });
  </script>
</body>
</html>
