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
    document.getElementById("statOpen").innerText = jobs.filter(isActiveJob).filter(j => j.status === "OPEN").length;
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

// A job is active if it is still OPEN/FULL and its date + end time have not passed.
function isActiveJob(job) {
  if (job.status !== "OPEN" && job.status !== "FULL") return false;
  if (!job.jobDate) return true;
  const end = new Date(`${job.jobDate}T${job.endTime || "23:59:59"}`);
  return isNaN(end.getTime()) || end >= new Date();
}

function renderMyJobs(allJobs) {
  const container = document.getElementById("myJobsList");
  // Dashboard cards show active jobs only. Stats above still use the full list.
  const jobs = (allJobs || []).filter(isActiveJob);
  if (jobs.length === 0) {
    container.innerHTML = '<div class="col-12 text-muted">You have no active jobs right now. <a href="post-job.html">Post a job</a>.</div>';
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
          ${job.status !== "COMPLETED" && job.status !== "CANCELLED" ? `<button type="button" class="btn btn-sm btn-outline-danger" onclick="deleteEmployerJob(${job.id})" aria-label="Delete ${job.title}"><i class="bi bi-trash"></i></button>` : ""}
        </div>
      </div>
    </div>`).join("");
}

async function deleteEmployerJob(jobId) {
  if (!confirm("Delete this job? It will be removed from the public Jobs page. Existing applications will remain available in their related sections.")) return;
  try {
    await WS.del(`/jobs/${jobId}`);
    WS.showAlert("myJobsAlert", "Job deleted. It is no longer visible to workers and now shows as Deleted in My Jobs.", "success");
    const jobs = await WS.get("/jobs/my");
    document.getElementById("statPosted").innerText = jobs.length;
    document.getElementById("statOpen").innerText = jobs.filter(isActiveJob).filter(j => j.status === "OPEN").length;
    document.getElementById("statCompleted").innerText = jobs.filter(j => j.status === "COMPLETED").length;
    renderMyJobs(jobs);
  } catch (e) {
    WS.showAlert("myJobsAlert", "Could not delete job: " + e.message);
  }
}
