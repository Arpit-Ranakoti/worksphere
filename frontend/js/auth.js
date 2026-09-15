// ============================
// WorkSphere - Auth (Login / Register)
// ============================

document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");

  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const email = document.getElementById("loginEmail").value.trim();
      const password = document.getElementById("loginPassword").value;

      try {
        const res = await WS.request("/auth/login", {
          method: "POST",
          body: { email, password },
          auth: false
        });
        WS.setSession(res.token, res.user);
        redirectByRole(res.user.role);
      } catch (err) {
        WS.showAlert("loginAlert", err.message);
      }
    });
  }

  if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const fullName = document.getElementById("regName").value.trim();
      const email = document.getElementById("regEmail").value.trim();
      const phone = document.getElementById("regPhone").value.trim();
      const password = document.getElementById("regPassword").value;
      const role = document.getElementById("regRole").value;

      if (password.length < 6) {
        WS.showAlert("registerAlert", "Password must be at least 6 characters.");
        return;
      }

      try {
        const res = await WS.request("/auth/register", {
          method: "POST",
          body: { fullName, email, phone, password, role },
          auth: false
        });
        WS.setSession(res.token, res.user);
        redirectByRole(res.user.role);
      } catch (err) {
        WS.showAlert("registerAlert", err.message);
      }
    });
  }
});

function redirectByRole(role) {
  if (role === "JOB_SEEKER") window.location.href = "worker-dashboard.html";
  else if (role === "EMPLOYER") window.location.href = "employer-dashboard.html";
  else if (role === "ADMIN") window.location.href = "admin-dashboard.html";
  else window.location.href = "index.html";
}