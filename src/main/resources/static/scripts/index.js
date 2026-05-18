/**
 * Login - authenticated backend session bootstrap
 */
document.addEventListener("DOMContentLoaded", function () {
  var form = document.getElementById("login-form");
  if (!form) return;

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    var email = document.getElementById("email");
    var password = document.getElementById("password");
    if (!email.value.trim() || !password.value.trim()) {
      if (window.HMS) HMS.showToast("Please enter email and password.", "error");
      return;
    }

    if (window.HMS) HMS.showLoading();
    HMS.api("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({
        email: email.value.trim(),
        password: password.value,
        rememberMe: !!document.querySelector('input[name="remember"]:checked'),
      }),
    })
      .then(function (session) {
        localStorage.setItem(HMS.KEYS.session, JSON.stringify(Object.assign({ loggedIn: true }, session)));
        HMS.showToast("Signed in successfully.", "success");
        window.location.href = "dashboard.html";
      })
      .catch(function (err) {
        HMS.showToast(err.message || "Login failed.", "error");
      })
      .finally(function () {
        HMS.hideLoading();
      });
  });
});

