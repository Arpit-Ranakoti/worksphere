// ============================
// WorkSphere - Jobs listing & filters (jobs.html)
// ============================

async function loadJobs() {
  const container = document.getElementById("jobsList");
  container.innerHTML = '<div class="col-12 text-center text-muted">Loading jobs...</div>';

  const params = new URLSearchParams();
  const keyword = document.getElementById("fKeyword")?.value.trim();
  const location = document.getElementById("fLocation")?.value.trim();
  const category = document.getElementById("fCategory")?.value;
  const date = document.getElementById("fDate")?.value;
  const minPay = document.getElementById("fMinPay")?.value;
  const maxPay = document.getElementById("fMaxPay")?.value;

  if (keyword) params.append("keyword", keyword);
  if (location) params.append("location", location);
  if (category) params.append("category", category);
  if (date) params.append("date", date);
  if (minPay) params.append("minPay", minPay);
  if (maxPay) params.append("maxPay", maxPay);

  try {
    const isLoggedIn = WS.isLoggedIn();
    const jobs = await WS.request(`/jobs?${params.toString()}`, { auth: isLoggedIn });
    const list = jobs.content || jobs;
    renderJobs(list);
  } catch (err) {
    WS.showAlert("jobsAlert", "Could not load jobs: " + err.message);
    container.innerHTML = "";
  }
}

function renderJobs(list) {
  const container = document.getElementById("jobsList");
  if (!list || list.length === 0) {
    container.innerHTML = '<div class="col-12 text-center text-muted">No jobs found matching your filters.</div>';
    return;
  }

  container.innerHTML = list.map(job => {
    const matchHtml = (job.matchScore !== undefined && job.matchScore !== null)
      ? `<span class="${WS.matchBadgeClass(job.matchScore)}">${Math.round(job.matchScore)}% Match</span>`
      : "";
    const skills = (job.requiredSkills || []).map(s =>
      `<span class="badge badge-skill">${s}</span>`).join(" ");

    return `
      <div class="col-md-4">
        <div class="card-ws job-card p-3 h-100 d-flex flex-column">
          <div class="d-flex justify-content-between align-items-start mb-2">
            <div class="job-title">${job.title}</div>
            ${matchHtml}
          </div>
          <div class="job-meta mb-2">
            <div><i class="bi bi-tag"></i> ${job.category}</div>
            <div><i class="bi bi-geo-alt"></i> ${job.location}</div>
            <div><i class="bi bi-calendar"></i> ${WS.formatDate(job.jobDate)}</div>
            <div><i class="bi bi-clock"></i> ${job.startTime} - ${job.endTime}</div>
          </div>
          <div class="fw-bold text-navy mb-2">₹${job.pay} &nbsp; <span class="text-muted fw-normal small">${job.workersSelected}/${job.workersRequired} filled</span></div>
          <div class="mb-3">${skills || '<span class="text-muted small">No specific skills required</span>'}</div>
          <div class="mt-auto d-flex justify-content-between align-items-center">
            <span class="badge badge-status-${job.status}">${job.status}</span>
            <a href="job-details.html?id=${job.id}" class="btn btn-sm btn-accent">View Details</a>
          </div>
        </div>
      </div>`;
  }).join("");
}