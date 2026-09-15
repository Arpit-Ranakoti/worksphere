// ============================
// WorkSphere - Worker Dashboard
// ============================

(async function initWorkerDashboard() {
  const user = WS.requireAuth(["JOB_SEEKER"]);
  if (!user) return;

  WS.renderNavbar("navbar", "dashboard");
  WS.renderFooter("footer");
  document.getElementById("welcomeMsg").innerText = `Welcome, ${user.fullName || user.name}!`;

  try {
    const profile = await WS.get("/users/me/worker-profile");
    document.getElementById("statRating").innerText = profile.rating != null ? "⭐ " + profile.rating.toFixed(1) : "New";
    document.getElementById("statStrikes").innerText = profile.strikeCount ?? 0;
  } catch (e) { /* profile may not exist yet */ }

  try {
    const apps = await WS.get("/applications/my");
    const pending = apps.filter(a => a.status === "PENDING").length;
    const selected = apps.filter(a => a.status === "SELECTED").length;
    document.getElementById("statPending").innerText = pending;
    document.getElementById("statSelected").innerText = selected;
  } catch (e) {
    document.getElementById("statPending").innerText = "0";
    document.getElementById("statSelected").innerText = "0";
  }

  try {
    const jobs = await WS.get("/jobs?recommended=true&size=6");
    const list = jobs.content || jobs;
    renderRecommended(list);
  } catch (e) {
    document.getElementById("recommendedJobs").innerHTML =
      `<div class="col-12 text-danger">Could not load jobs: ${e.message}</div>`;
  }

  try {
    const commitments = await WS.get("/commitments/my?upcoming=true");
    renderUpcoming(commitments);
  } catch (e) {
    document.getElementById("upcomingCommitments").innerHTML =
      `<div class="text-muted">No upcoming commitments.</div>`;
  }
})();

function renderRecommended(list) {
  const container = document.getElementById("recommendedJobs");
  if (!list || list.length === 0) {
    container.innerHTML = '<div class="col-12 text-muted">No recommended jobs right now. <a href="jobs.html">Browse all jobs</a>.</div>';
    return;
  }
  container.innerHTML = list.map(job => `
    <div class="col-md-4">
      <div class="card-ws job-card p-3 h-100">
        <div class="d-flex justify-content-between">
          <div class="job-title">${job.title}</div>
          <span class="${WS.matchBadgeClass(job.matchScore || 0)}">${Math.round(job.matchScore || 0)}% Match</span>
        </div>
        <div class="job-meta my-2">
          <i class="bi bi-geo-alt"></i> ${job.location} &nbsp; <i class="bi bi-calendar"></i> ${WS.formatDate(job.jobDate)}
        </div>
        <div class="fw-bold text-navy mb-2">₹${job.pay}</div>
        <a href="job-details.html?id=${job.id}" class="btn btn-sm btn-accent">View Details</a>
      </div>
    </div>`).join("");
}

function renderUpcoming(list) {
  const container = document.getElementById("upcomingCommitments");
  if (!list || list.length === 0) {
    container.innerHTML = '<div class="text-muted">No upcoming commitments.</div>';
    return;
  }
  container.innerHTML = list.map(c => `
    <div class="list-group-item d-flex justify-content-between align-items-center">
      <div>
        <div class="fw-semibold">${c.jobTitle}</div>
        <div class="text-muted small"><i class="bi bi-geo-alt"></i> ${c.jobLocation} &nbsp; <i class="bi bi-calendar"></i> ${WS.formatDate(c.jobDate)}</div>
      </div>
      ${WS.statusBadge(c.status)}
    </div>`).join("");
}