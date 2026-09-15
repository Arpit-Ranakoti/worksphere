// ============================
// WorkSphere - Disputes
// ============================

let disputeModal;

(async function initDisputesPage() {
  const user = WS.requireAuth(["JOB_SEEKER", "EMPLOYER"]);
  if (!user) return;

  WS.renderNavbar("navbar", "disputes");
  WS.renderFooter("footer");
  disputeModal = document.getElementById("newDisputeModal");

  await loadDisputes();
  await loadCommitmentOptions();
})();

async function loadDisputes() {
  const container = document.getElementById("disputesContainer");
  try {
    const disputes = await WS.get("/disputes/my");
    if (!disputes || disputes.length === 0) {
      container.innerHTML = '<div class="text-muted">You have not raised any disputes.</div>';
      return;
    }
    container.innerHTML = disputes.map(d => `
      <div class="card-ws p-3 mb-3">
        <div class="d-flex justify-content-between flex-wrap">
          <div class="fw-bold">Dispute #${d.id} — ${d.reason}</div>
          ${WS.statusBadge(d.status)}
        </div>
        <div class="text-muted small mb-2">Job: ${d.jobTitle || "-"} | Filed: ${WS.formatDateTime(d.createdAt)}</div>
        <p class="mb-2">${d.description}</p>
        ${d.adminResolution ? `<div class="alert alert-secondary py-2 mb-0">Admin Resolution: ${d.adminResolution}</div>` : '<div class="text-muted small">Awaiting admin review.</div>'}
      </div>`).join("");
  } catch (e) {
    container.innerHTML = `<div class="text-danger">${e.message}</div>`;
  }
}

async function loadCommitmentOptions() {
  const select = document.getElementById("disputeCommitment");
  try {
    const commitments = await WS.get("/commitments/my");
    if (!commitments || commitments.length === 0) {
      select.innerHTML = '<option value="">No commitments available</option>';
      return;
    }
    select.innerHTML = commitments.map(c =>
      `<option value="${c.id}">${c.jobTitle} — ${WS.formatDate(c.jobDate)}</option>`).join("");
  } catch (e) {
    select.innerHTML = '<option value="">Could not load commitments</option>';
  }
}

async function submitDispute() {
  const commitmentId = document.getElementById("disputeCommitment").value;
  const reason = document.getElementById("disputeReason").value;
  const description = document.getElementById("disputeDescription").value.trim();
  const evidence = document.getElementById("disputeEvidence").value.trim();

  if (!commitmentId || !description) {
    alert("Please select a commitment and enter a description.");
    return;
  }

  try {
    await WS.post("/disputes", {
      commitmentId: Number(commitmentId),
      reason,
      description,
      evidence
    });
    bootstrap.Modal.getInstance(disputeModal).hide();
    document.getElementById("newDisputeForm").reset();
    loadDisputes();
  } catch (e) {
    alert("Could not submit dispute: " + e.message);
  }
}