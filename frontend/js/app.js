// ============================
// WorkSphere - Shared App Utilities
// Handles: auth token storage, fetch wrapper, navbar rendering, alerts
// ============================

const WS = {
  // ---- Auth/session helpers ----
  getToken() {
    return localStorage.getItem("ws_token");
  },
  getUser() {
    const raw = localStorage.getItem("ws_user");
    return raw ? JSON.parse(raw) : null;
  },
  setSession(token, user) {
    localStorage.setItem("ws_token", token);
    localStorage.setItem("ws_user", JSON.stringify(user));
  },
  clearSession() {
    localStorage.removeItem("ws_token");
    localStorage.removeItem("ws_user");
  },
  isLoggedIn() {
    return !!this.getToken();
  },
  requireAuth(allowedRoles) {
    const user = this.getUser();
    if (!this.isLoggedIn() || !user) {
      window.location.href = "login.html";
      return null;
    }
    if (allowedRoles && !allowedRoles.includes(user.role)) {
      window.location.href = "index.html";
      return null;
    }
    return user;
  },
  logout() {
    this.clearSession();
    window.location.href = "login.html";
  },

  // ---- Core fetch wrapper ----
  async request(path, { method = "GET", body = null, auth = true } = {}) {
    const headers = { "Content-Type": "application/json" };
    if (auth && this.getToken()) {
      headers["Authorization"] = "Bearer " + this.getToken();
    }

    let response;
    try {
      response = await fetch(`${API_BASE_URL}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : null,
      });
    } catch (err) {
      throw new Error("Cannot reach server. Is the backend running?");
    }

    let data = null;
    try {
      data = await response.json();
    } catch (e) {
      data = null;
    }

    if (response.status === 401) {
      this.clearSession();
      window.location.href = "login.html";
      throw new Error("Session expired. Please log in again.");
    }

    if (!response.ok) {
      const message = (data && data.message) || "Something went wrong.";
      throw new Error(message);
    }

    return data;
  },

  get(path) { return this.request(path, { method: "GET" }); },
  post(path, body) { return this.request(path, { method: "POST", body }); },
  put(path, body) { return this.request(path, { method: "PUT", body }); },
  del(path) { return this.request(path, { method: "DELETE" }); },

  // ---- UI helpers ----
  showAlert(containerId, message, type = "danger") {
    const container = document.getElementById(containerId);
    if (!container) { alert(message); return; }
    container.innerHTML = `
      <div class="alert alert-${type} alert-dismissible fade show" role="alert">
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>`;
  },

  matchBadgeClass(score) {
    if (score >= 70) return "match-badge";
    if (score >= 40) return "match-badge low";
    return "match-badge verylow";
  },

  formatDate(dateStr) {
    if (!dateStr) return "";
    const d = new Date(dateStr);
    return d.toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
  },

  formatDateTime(dateStr) {
    if (!dateStr) return "";
    const d = new Date(dateStr);
    return d.toLocaleString("en-IN", { day: "numeric", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" });
  },

  statusBadge(status) {
    return `<span class="badge badge-status-${status}">${status.replace(/_/g, " ")}</span>`;
  },

  // ---- Navbar rendering ----
  renderNavbar(containerId, activePage = "") {
    const user = this.getUser();
    const container = document.getElementById(containerId);
    if (!container) return;

    let links = "";
    if (!user) {
      links = `
        <a class="nav-link" href="jobs.html">Browse Jobs</a>
        <a class="nav-link" href="login.html">Login</a>
        <a class="btn btn-accent btn-sm ms-2 px-3" href="register.html">Sign Up</a>`;
    } else if (user.role === "JOB_SEEKER") {
      links = `
        <a class="nav-link ${activePage==='jobs'?'active':''}" href="jobs.html">Jobs</a>
        <a class="nav-link ${activePage==='applications'?'active':''}" href="applications.html">My Applications</a>
        <a class="nav-link ${activePage==='commitments'?'active':''}" href="commitments.html">Commitments</a>
        <a class="nav-link ${activePage==='notifications'?'active':''}" href="notifications.html">Notifications</a>
        <a class="nav-link ${activePage==='profile'?'active':''}" href="worker-profile.html">Profile</a>
        <a class="nav-link" href="#" onclick="WS.logout();return false;">Logout</a>`;
    } else if (user.role === "EMPLOYER") {
      links = `
        <a class="nav-link ${activePage==='dashboard'?'active':''}" href="employer-dashboard.html">Dashboard</a>
        <a class="nav-link ${activePage==='myjobs'?'active':''}" href="employer-dashboard.html#myjobs">My Jobs</a>
        <a class="nav-link ${activePage==='postjob'?'active':''}" href="post-job.html">Post Job</a>
        <a class="nav-link ${activePage==='applications'?'active':''}" href="applications.html">Applications</a>
        <a class="nav-link ${activePage==='commitments'?'active':''}" href="commitments.html">Commitments</a>
        <a class="nav-link ${activePage==='notifications'?'active':''}" href="notifications.html">Notifications</a>
        <a class="nav-link ${activePage==='profile'?'active':''}" href="employer-profile.html">Profile</a>
        <a class="nav-link" href="#" onclick="WS.logout();return false;">Logout</a>`;
    } else if (user.role === "ADMIN") {
      links = `
        <a class="nav-link ${activePage==='dashboard'?'active':''}" href="admin-dashboard.html">Dashboard</a>
        <a class="nav-link" href="#" onclick="WS.logout();return false;">Logout</a>`;
    }

    container.innerHTML = `
      <nav class="navbar navbar-expand-lg navbar-worksphere">
        <div class="container">
          <a class="navbar-brand" href="index.html">WorkSphere</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#wsNavbar" style="filter:invert(1);">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="wsNavbar">
            <div class="navbar-nav ms-auto align-items-lg-center gap-1">
              ${links}
            </div>
          </div>
        </div>
      </nav>`;
  },

  renderFooter(containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    container.innerHTML = `
      <footer class="footer-ws text-center">
        <div class="container">
          <strong class="text-white">WorkSphere</strong> — Find work. Make today count.
          <div class="small mt-1">College PBL Project &copy; ${new Date().getFullYear()}</div>
        </div>
      </footer>`;
  }
};