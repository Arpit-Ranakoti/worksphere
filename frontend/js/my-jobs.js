// ============================
// WorkSphere - My Jobs (job history for employers)
// ============================

const JOB_FILTERS = [
  { key: "ALL", label: "All" },
  { key: "ACTIVE", label: "Active" },
  { key: "COMPLETED", label: "Completed" },
  { key: "EXPIRED", label: "Expired" },
  { key: "DELETED", label: "Deleted" }
];

let allMyJobs = [];
let currentFilter = "ALL";

(async function initMyJobs() {
  const user = WS.requireAuth(["EMPLOYER"]);
  if (!user) return;

  WS.renderNavbar("navbar", "myjobs");
  WS.renderFooter("footer");

  try {
    allMyJobs = (await WS.get("/jobs/my")) || [];
    renderFilters();
    renderJobs();
  } catch (e) {
    document.getElementById("myJobsHistory").innerHTML =
      `<div class="col-12 text-danger">Could not load jobs: ${esc(e.message)}</div>`;
  }
})();

function esc(value) {
  return String(value == null ? "" : value)
    .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

// What the employer sees for each job.
// ACTIVE / COMPLETED / EXPIRED / DELETED each have their own tab. The backend sets
// EXPIRED once the date has passed; the date check below only covers the short gap
// before that happens. CANCELLED (rare) is shown under All only.
function displayStatus(job) {
  if (job.status === "DELETED") return { key: "DELETED", label: "Deleted" };
  if (job.status === "COMPLETED") return { key: "COMPLETED", label: "Completed" };
  if (job.status === "CANCELLED") return { key: "CANCELLED", label: "Cancelled" };
  if (job.status === "EXPIRED" || WS.hasJobEnded(job)) return { key: "EXPIRED", label: "Expired" };
  return { key: "ACTIVE", label: "Active" };
}

function filterMatches(job, filterKey) {
  return filterKey === "ALL" || displayStatus(job).key === filterKey;
}

function renderFilters() {
  document.getElementById("jobFilters").innerHTML = JOB_FILTERS.map(f => {
    const count = allMyJobs.filter(j => filterMatches(j, f.key)).length;
    return `
      <li class="nav-item">
        <button type="button" class="nav-link ${f.key === currentFilter ? "active" : ""}" onclick="setJobFilter('${f.key}')">
          ${f.label} <span class="badge bg-light text-dark ms-1">${count}</span>
        </button>
      </li>`;
  }).join("");
}

function setJobFilter(key) {
  currentFilter = key;
  renderFilters();
  renderJobs();
}

function renderJobs() {
  const container = document.getElementById("myJobsHistory");
  const jobs = allMyJobs.filter(j => filterMatches(j, currentFilter));

  if (jobs.length === 0) {
    container.innerHTML = allMyJobs.length === 0
      ? '<div class="col-12 text-muted">You have not posted any jobs yet. <a href="post-job.html">Post your first job</a>.</div>'
      : '<div class="col-12 text-muted">No jobs in this category.</div>';
    return;
  }

  container.innerHTML = jobs.map(job => {
    const st = displayStatus(job);
    const isDeleted = st.key === "DELETED";
    return `
      <div class="col-md-4">
        <div class="card-ws job-card p-3 h-100 d-flex flex-column ${isDeleted ? "opacity-75" : ""}">
          <div class="d-flex justify-content-between gap-2">
            <div class="job-title">${esc(job.title)}</div>
            <span class="badge badge-status-${st.key} align-self-start">${st.label}</span>
          </div>
          <div class="job-meta my-2">
            <i class="bi bi-geo-alt"></i> ${esc(job.location)} &nbsp;
            <i class="bi bi-calendar"></i> ${WS.formatDate(job.jobDate)}
          </div>
          <div class="fw-bold text-navy mb-2">₹${esc(job.pay)} &nbsp;
            <span class="text-muted fw-normal small">${job.workersSelected}/${job.workersRequired} filled</span>
          </div>
          <div class="text-muted small mb-2">${job.applicationCount || 0} applicant(s)</div>
          <div class="mt-auto d-flex gap-2">
            ${isDeleted ? "" : `<a href="applications.html?jobId=${job.id}" class="btn btn-sm btn-accent flex-fill">Applicants</a>`}
            <a href="job-details.html?id=${job.id}" class="btn btn-sm btn-outline-secondary flex-fill">View</a>
          </div>
        </div>
      </div>`;
  }).join("");
}
