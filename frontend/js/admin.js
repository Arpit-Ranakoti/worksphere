// ============================
// WorkSphere - Admin Dashboard
// ============================

(function initAdminDashboard() {
  const user = WS.requireAuth(["ADMIN"]);
  if (!user) return;

  document.querySelectorAll(".sidebar-ws a[data-tab]").forEach(link => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      document.querySelectorAll(".sidebar-ws a[data-tab]").forEach(a => a.classList.remove("active"));
      link.classList.add("active");
      document.querySelectorAll(".admin-tab").forEach(t => t.classList.add("d-none"));
      const tab = link.getAttribute("data-tab");
      document.getElementById("tab-" + tab).classList.remove("d-none");
      loadTab(tab);
    });
  });

  loadTab("dashboard");
})();

async function loadTab(tab) {
  try {
    if (tab === "dashboard") return loadDashboardStats();
    if (tab === "users") return loadUsers();
    if (tab === "jobs") return loadJobsTable();
    if (tab === "applications") return loadApplicationsTable();
    if (tab === "commitments") return loadCommitmentsTable();
    if (tab === "disputes") return loadDisputesTable();
    if (tab === "ratings") return loadRatingsTable();
    if (tab === "strikes") return loadStrikesTable();
  } catch (err) {
    console.error(err);
  }
}

async function loadDashboardStats() {
  try {
    const stats = await WS.get("/admin/dashboard");
    document.getElementById("statUsers").innerText = stats.totalUsers ?? 0;
    document.getElementById("statWorkers").innerText = stats.totalWorkers ?? 0;
    document.getElementById("statEmployers").innerText = stats.totalEmployers ?? 0;
    document.getElementById("statOpenJobs").innerText = stats.openJobs ?? 0;
    document.getElementById("statCompletedJobs").innerText = stats.completedJobs ?? 0;
    document.getElementById("statOpenDisputes").innerText = stats.openDisputes ?? 0;
  } catch (e) { console.error(e); }
}

async function loadUsers() {
  const tbody = document.getElementById("usersTableBody");
  try {
    const users = await WS.get("/admin/users");
    tbody.innerHTML = users.map(u => `
      <tr>
        <td>${u.id}</td><td>${u.fullName || u.name}</td><td>${u.email}</td>
        <td><span class="badge bg-secondary">${u.role}</span></td>
        <td>${u.active ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-danger">Inactive</span>'}</td>
      </tr>`).join("");
  } catch (e) { tbody.innerHTML = `<tr><td colspan="5" class="text-danger">${e.message}</td></tr>`; }
}

async function loadJobsTable() {
  const tbody = document.getElementById("jobsTableBody");
  try {
    const jobs = await WS.get("/admin/jobs");
    tbody.innerHTML = jobs.map(j => `
      <tr>
        <td>${j.id}</td><td>${j.title}</td><td>${j.employerName || "-"}</td>
        <td>${j.location}</td><td>${WS.formatDate(j.jobDate)}</td>
        <td>${WS.statusBadge(j.status)}</td>
      </tr>`).join("");
  } catch (e) { tbody.innerHTML = `<tr><td colspan="6" class="text-danger">${e.message}</td></tr>`; }
}

async function loadApplicationsTable() {
  const tbody = document.getElementById("applicationsTableBody");
  try {
    const apps = await WS.get("/admin/applications");
    tbody.innerHTML = apps.map(a => `
      <tr>
        <td>${a.id}</td><td>${a.jobTitle}</td><td>${a.workerName}</td>
        <td>${WS.statusBadge(a.status)}</td><td>${WS.formatDateTime(a.appliedAt)}</td>
      </tr>`).join("");
  } catch (e) { tbody.innerHTML = `<tr><td colspan="5" class="text-danger">${e.message}</td></tr>`; }
}

async function loadCommitmentsTable() {
  const tbody = document.getElementById("commitmentsTableBody");
  try {
    const commitments = await WS.get("/admin/commitments");
    tbody.innerHTML = commitments.map(c => `
      <tr>
        <td>${c.id}</td><td>${c.jobTitle}</td><td>${c.workerName}</td>
        <td>${WS.statusBadge(c.status)}</td><td>${c.depositStatus || "-"}</td>
      </tr>`).join("");
  } catch (e) { tbody.innerHTML = `<tr><td colspan="5" class="text-danger">${e.message}</td></tr>`; }
}

async function loadRatingsTable() {
  const tbody = document.getElementById("ratingsTableBody");
  try {
    const ratings = await WS.get("/admin/ratings");
    tbody.innerHTML = ratings.map(r => `
      <tr>
        <td>${r.id}</td><td>${r.jobTitle}</td><td>${r.fromUserName}</td><td>${r.toUserName}</td>
        <td>⭐ ${r.rating}</td><td>${r.review || "-"}</td>
      </tr>`).join("");
  } catch (e) { tbody.innerHTML = `<tr><td colspan="6" class="text-danger">${e.message}</td></tr>`; }
}

async function loadStrikesTable() {
  const tbody = document.getElementById("strikesTableBody");
  try {
    const strikes = await WS.get("/admin/strikes");
    tbody.innerHTML = strikes.map(s => `
      <tr>
        <td>${s.id}</td><td>${s.userName}</td><td>${s.jobTitle || "-"}</td>
        <td>${s.reason}</td><td>${s.severity}</td><td>${WS.statusBadge(s.status)}</td>
      </tr>`).join("");
  } catch (e) { tbody.innerHTML = `<tr><td colspan="6" class="text-danger">${e.message}</td></tr>`; }
}

async function loadDisputesTable() {
  const container = document.getElementById("disputesContainer");
  try {
    const disputes = await WS.get("/admin/disputes");
    if (!disputes || disputes.length === 0) {
      container.innerHTML = '<div class="text-muted">No disputes.</div>';
      return;
    }
    container.innerHTML = disputes.map(d => `
      <div class="card-ws p-3 mb-3">
        <div class="d-flex justify-content-between">
          <div class="fw-bold">Dispute #${d.id} — ${d.reason}</div>
          ${WS.statusBadge(d.status)}
        </div>
        <div class="text-muted small mb-2">Job: ${d.jobTitle || "-"} | Raised by: ${d.raisedByName} | Against: ${d.againstUserName}</div>
        <p>${d.description}</p>
        ${d.adminResolution ? `<div class="alert alert-secondary py-2">Resolution: ${d.adminResolution}</div>` : ""}
        ${d.status !== "RESOLVED" && d.status !== "REJECTED" ? `
        <div class="d-flex gap-2 mt-2">
          <button class="btn btn-sm btn-success" onclick="resolveDispute(${d.id}, 'RESOLVE_WORKER')">Resolve for Worker</button>
          <button class="btn btn-sm btn-primary" onclick="resolveDispute(${d.id}, 'RESOLVE_EMPLOYER')">Resolve for Employer</button>
          <button class="btn btn-sm btn-outline-danger" onclick="resolveDispute(${d.id}, 'DISMISS')">Dismiss</button>
        </div>` : ""}
      </div>`).join("");
  } catch (e) { container.innerHTML = `<div class="text-danger">${e.message}</div>`; }
}

async function resolveDispute(id, action) {
  const note = prompt("Enter a resolution note for this dispute:", "");
  if (note === null) return;
  try {
    await WS.put(`/admin/disputes/${id}/resolve`, { action, resolutionNote: note });
    loadDisputesTable();
  } catch (e) {
    alert("Could not resolve dispute: " + e.message);
  }
}