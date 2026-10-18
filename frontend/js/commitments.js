// ============================
// WorkSphere - Commitments
// ============================

let ratingModal;

(async function initCommitmentsPage() {
  const user = WS.requireAuth(["JOB_SEEKER", "EMPLOYER"]);
  if (!user) return;

  WS.renderNavbar("navbar", "commitments");
  WS.renderFooter("footer");
  ratingModal = new bootstrap.Modal(document.getElementById("ratingModal"));

  await loadCommitments();
})();

async function loadCommitments() {
  const container = document.getElementById("commitmentsContainer");
  try {
    const commitments = await WS.get("/commitments/my");
    if (!commitments || commitments.length === 0) {
      container.innerHTML = '<div class="text-muted">No commitments yet.</div>';
      return;
    }
    const user = WS.getUser();
    container.innerHTML = commitments.map(c => renderCommitmentCard(c, user.role)).join("");
  } catch (e) {
    container.innerHTML = `<div class="text-danger">${e.message}</div>`;
  }
}

// Active "Rate" button, or a light, disabled "Rated" button once the user has already rated
function ratingButton(c, label) {
  if (c.ratedByMe) {
    return `<button class="btn btn-sm btn-light border text-muted" disabled title="You have already rated this job"><i class="bi bi-check2-circle"></i> Rated</button>`;
  }
  return `<button class="btn btn-sm btn-accent" onclick="openRatingModal(${c.id}, ${c.otherPartyUserId})">${label}</button>`;
}

function renderCommitmentCard(c, role) {
  let actions = "";

  if (role === "JOB_SEEKER") {
    if (c.status === "PENDING_CONFIRMATION") {
      actions = `
        <button class="btn btn-sm btn-success" onclick="confirmCommitment(${c.id})">Confirm Commitment</button>
        <button class="btn btn-sm btn-outline-danger" onclick="declineCommitment(${c.id})">Decline</button>`;
    } else if (c.status === "CONFIRMED") {
      actions = `<button class="btn btn-sm btn-outline-danger" onclick="cancelCommitment(${c.id})">Cancel</button>`;
    } else if (c.status === "COMPLETED") {
      actions = ratingButton(c, "Rate Employer");
    }
  } else if (role === "EMPLOYER") {
    if (c.status === "CONFIRMED") {
      actions = `
        <button class="btn btn-sm btn-success" onclick="completeCommitment(${c.id})">Mark Completed</button>
        <button class="btn btn-sm btn-outline-danger" onclick="reportNoShow(${c.id})">Report No-Show</button>`;
    } else if (c.status === "COMPLETED") {
      actions = ratingButton(c, "Rate Worker");
    }
  }

  return `
    <div class="card-ws p-3 mb-3">
      <div class="d-flex justify-content-between flex-wrap">
        <div>
          <div class="fw-bold">${c.jobTitle}</div>
          <div class="text-muted small"><i class="bi bi-geo-alt"></i> ${c.jobLocation} &nbsp; <i class="bi bi-calendar"></i> ${WS.formatDate(c.jobDate)}</div>
          <div class="text-muted small">${role === "JOB_SEEKER" ? "Employer" : "Worker"}: ${c.otherPartyName}</div>
          ${c.depositStatus ? `<div class="small">Deposit: ${c.depositStatus}</div>` : ""}
        </div>
        <div class="d-flex flex-column align-items-end gap-2">
          ${WS.statusBadge(c.status)}
          <div class="d-flex gap-2">${actions}</div>
        </div>
      </div>
    </div>`;
}

async function confirmCommitment(id) {
  try {
    await WS.post(`/commitments/${id}/confirm`, {});
    loadCommitments();
  } catch (e) { WS.showAlert("commitAlert", "Could not confirm: " + e.message); }
}

async function declineCommitment(id) {
  if (!confirm("Decline this commitment?")) return;
  try {
    await WS.post(`/commitments/${id}/decline`, {});
    loadCommitments();
  } catch (e) { WS.showAlert("commitAlert", "Could not decline: " + e.message); }
}

async function cancelCommitment(id) {
  const reason = prompt("Reason for cancellation (helps fair-review process):", "");
  if (reason === null) return;
  try {
    await WS.post(`/commitments/${id}/cancel`, { reason });
    loadCommitments();
  } catch (e) { WS.showAlert("commitAlert", "Could not cancel: " + e.message); }
}

async function completeCommitment(id) {
  if (!confirm("Mark this commitment as completed?")) return;
  try {
    await WS.post(`/commitments/${id}/complete`, {});
    loadCommitments();
  } catch (e) { WS.showAlert("commitAlert", "Could not mark completed: " + e.message); }
}

async function reportNoShow(id) {
  const details = prompt("Describe what happened (this creates a dispute for admin review):", "");
  if (details === null) return;
  try {
    await WS.post("/disputes", {
      commitmentId: id,
      reason: "No-Show",
      description: details
    });
    WS.showAlert("commitAlert", "No-show reported. Admin will review the case.", "success");
    loadCommitments();
  } catch (e) { WS.showAlert("commitAlert", "Could not report no-show: " + e.message); }
}

function openRatingModal(commitmentId, otherUserId) {
  document.getElementById("ratingCommitmentId").value = commitmentId;
  document.getElementById("ratingToUserId").value = otherUserId;
  document.getElementById("ratingStars").value = "5";
  document.getElementById("ratingReview").value = "";
  ratingModal.show();
}

async function submitRating() {
  const commitmentId = document.getElementById("ratingCommitmentId").value;
  const toUserId = document.getElementById("ratingToUserId").value;
  const rating = Number(document.getElementById("ratingStars").value);
  const review = document.getElementById("ratingReview").value;

  try {
    await WS.post("/ratings", { commitmentId: Number(commitmentId), toUserId: Number(toUserId), rating, review });
    ratingModal.hide();
    loadCommitments();
  } catch (e) {
    alert("Could not submit rating: " + e.message);
  }
}