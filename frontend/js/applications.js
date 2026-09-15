// ============================
// WorkSphere - Applications
// Handles BOTH worker view ("My Applications") and employer view
// (applicants for a specific job, via ?jobId=)
// ============================

(async function initApplicationsPage() {
  const user = WS.requireAuth(["JOB_SEEKER", "EMPLOYER"]);
  if (!user) return;

  WS.renderNavbar("navbar", "applications");
  WS.renderFooter("footer");

  const params = new URLSearchParams(window.location.search);
  const jobId = params.get("jobId");

  if (user.role === "JOB_SEEKER") {
    document.getElementById("pageTitle").innerText = "My Applications";
    await loadMyApplications();
  } else if (user.role === "EMPLOYER") {
    if (jobId) {
      document.getElementById("pageTitle").innerText = "Applicants for This Job";
      await loadJobApplicants(jobId);
    } else {
      document.getElementById("pageTitle").innerText = "All Applications";
      await loadEmployerAllApplications();
    }
  }
})();

async function loadMyApplications() {
  const container = document.getElementById("applicationsContainer");
  try {
    const apps = await WS.get("/applications/my");
    if (!apps || apps.length === 0) {
      container.innerHTML = '<div class="text-muted">You have not applied to any jobs yet. <a href="jobs.html">Browse jobs</a>.</div>';
      return;
    }
    container.innerHTML = apps.map(a => `
      <div class="card-ws p-3 mb-3 d-flex flex-row justify-content-between align-items-center flex-wrap">
        <div>
          <div class="fw-bold">${a.jobTitle}</div>
          <div class="text-muted small"><i class="bi bi-geo-alt"></i> ${a.jobLocation} &nbsp; <i class="bi bi-calendar"></i> ${WS.formatDate(a.jobDate)}</div>
          <div class="small text-muted">Applied: ${WS.formatDateTime(a.appliedAt)}</div>
        </div>
        <div class="d-flex align-items-center gap-2">
          ${WS.statusBadge(a.status)}
          ${a.status === "PENDING" ? `<button class="btn btn-sm btn-outline-danger" onclick="withdrawApplication(${a.id})">Withdraw</button>` : ""}
          ${a.status === "SELECTED" ? `<a href="commitments.html" class="btn btn-sm btn-accent">Confirm Commitment</a>` : ""}
        </div>
      </div>`).join("");
  } catch (e) {
    container.innerHTML = `<div class="text-danger">${e.message}</div>`;
  }
}

async function withdrawApplication(id) {
  if (!confirm("Withdraw this application?")) return;
  try {
    await WS.put(`/applications/${id}/withdraw`, {});
    loadMyApplications();
  } catch (e) {
    WS.showAlert("appsAlert", "Could not withdraw: " + e.message);
  }
}

async function loadJobApplicants(jobId) {
  const container = document.getElementById("applicationsContainer");
  try {
    const apps = await WS.get(`/jobs/${jobId}/applications`);
    if (!apps || apps.length === 0) {
      container.innerHTML = '<div class="text-muted">No applicants yet for this job.</div>';
      return;
    }
    container.innerHTML = apps.map(a => `
      <div class="card-ws p-3 mb-3">
        <div class="d-flex justify-content-between flex-wrap">
          <div>
            <div class="fw-bold">${a.workerName}</div>
            <div class="text-muted small">⭐ ${a.workerRating != null ? a.workerRating.toFixed(1) : "New"} &nbsp; | &nbsp; Skills: ${(a.workerSkills || []).join(", ") || "None listed"}</div>
            <div class="small mt-1"><span class="${WS.matchBadgeClass(a.matchScore || 0)}">${Math.round(a.matchScore || 0)}% Match</span></div>
            ${a.coverMessage ? `<div class="small mt-2 fst-italic">"${a.coverMessage}"</div>` : ""}
          </div>
          <div class="d-flex flex-column align-items-end gap-2">
            ${WS.statusBadge(a.status)}
            ${a.status === "PENDING" ? `
              <div class="d-flex gap-2">
                <button class="btn btn-sm btn-success" onclick="selectApplicant(${a.id})">Select</button>
                <button class="btn btn-sm btn-outline-danger" onclick="rejectApplicant(${a.id})">Reject</button>
              </div>` : ""}
          </div>
        </div>
      </div>`).join("");
  } catch (e) {
    container.innerHTML = `<div class="text-danger">${e.message}</div>`;
  }
}

async function loadEmployerAllApplications() {
  const container = document.getElementById("applicationsContainer");
  try {
    const jobs = await WS.get("/jobs/my");
    if (!jobs || jobs.length === 0) {
      container.innerHTML = '<div class="text-muted">You have not posted any jobs yet.</div>';
      return;
    }
    container.innerHTML = jobs.map(j => `
      <div class="card-ws p-3 mb-3 d-flex flex-row justify-content-between align-items-center flex-wrap">
        <div>
          <div class="fw-bold">${j.title}</div>
          <div class="text-muted small">${j.applicationCount || 0} applicant(s)</div>
        </div>
        <a href="applications.html?jobId=${j.id}" class="btn btn-sm btn-accent">View Applicants</a>
      </div>`).join("");
  } catch (e) {
    container.innerHTML = `<div class="text-danger">${e.message}</div>`;
  }
}

async function selectApplicant(applicationId) {
  try {
    await WS.put(`/applications/${applicationId}/select`, {});
    const params = new URLSearchParams(window.location.search);
    loadJobApplicants(params.get("jobId"));
  } catch (e) {
    WS.showAlert("appsAlert", "Could not select applicant: " + e.message);
  }
}

async function rejectApplicant(applicationId) {
  if (!confirm("Reject this applicant?")) return;
  try {
    await WS.put(`/applications/${applicationId}/reject`, {});
    const params = new URLSearchParams(window.location.search);
    loadJobApplicants(params.get("jobId"));
  } catch (e) {
    WS.showAlert("appsAlert", "Could not reject applicant: " + e.message);
  }
}