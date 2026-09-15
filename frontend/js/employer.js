// ============================
// WorkSphere - Employer Dashboard
// ============================

(async function initEmployerDashboard() {
  const user = WS.requireAuth(["EMPLOYER"]);
  if (!user) return;

  WS.renderNavbar("navbar", "dashboard");
  WS.renderFooter("footer");
  document.getElementById("welcomeMsg").innerText = `Welcome, ${user.fullName || user.name}!`;

  try {
    const profile = await WS.get("/users/me/employer-profile");
    document.getElementById("statRating").innerText = profile.rating != null ? "⭐ " + profile.rating.toFixed(1) : "New";
  } catch (e) {}

  try {
    const jobs = await WS.get("/jobs/my");
    document.getElementById("statPosted").innerText = jobs.length;
    document.getElementById("statOpen").innerText = jobs.filter(j => j.status === "OPEN").length;
    document.getElementById("statCompleted").innerText = jobs.filter(j => j.status === "COMPLETED").length;
    renderMyJobs(jobs);

    let totalApps = 0, totalSelected = 0;
    for (const job of jobs) {
      totalApps += job.applicationCount || 0;
      totalSelected += job.workersSelected || 0;
    }
    document.getElementById("statApps").innerText = totalApps;
    document.getElementById("statSelected").innerText = totalSelected;
  } catch (e) {
    document.getElementById("myJobsList").innerHTML =
      `<div class="col-12 text-danger">Could not load jobs: ${e.message}</div>`;
  }
})();

function renderMyJobs(jobs) {
  const container = document.getElementById("myJobsList");
  if (!jobs || jobs.length === 0) {
    container.innerHTML = '<div class="col-12 text-muted">You have not posted any jobs yet. <a href="post-job.html">Post your first job</a>.</div>';
    return;
  }
  container.innerHTML = jobs.map(job => `
    <div class="col-md-4">
      <div class="card-ws job-card p-3 h-100 d-flex flex-column">
        <div class="d-flex justify-content-between">
          <div class="job-title">${job.title}</div>
          ${WS.statusBadge(job.status)}
        </div>
        <div class="job-meta my-2">
          <i class="bi bi-geo-alt"></i> ${job.location} &nbsp; <i class="bi bi-calendar"></i> ${WS.formatDate(job.jobDate)}
        </div>
        <div class="fw-bold text-navy mb-2">₹${job.pay} &nbsp; <span class="text-muted fw-normal small">${job.workersSelected}/${job.workersRequired} filled</span></div>
        <div class="text-muted small mb-2">${job.applicationCount || 0} applicant(s)</div>
        <div class="mt-auto d-flex gap-2">
          <a href="applications.html?jobId=${job.id}" class="btn btn-sm btn-accent flex-fill">Applicants</a>
          <a href="job-details.html?id=${job.id}" class="btn btn-sm btn-outline-secondary flex-fill">View</a>
        </div>
      </div>
    </div>`).join("");
}