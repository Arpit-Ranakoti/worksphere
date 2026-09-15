// ============================
// WorkSphere - Notifications
// ============================

(async function initNotificationsPage() {
  const user = WS.requireAuth(["JOB_SEEKER", "EMPLOYER", "ADMIN"]);
  if (!user) return;

  WS.renderNavbar("navbar", "notifications");
  WS.renderFooter("footer");
  await loadNotifications();
})();

async function loadNotifications() {
  const container = document.getElementById("notificationsContainer");
  try {
    const notifications = await WS.get("/notifications");
    if (!notifications || notifications.length === 0) {
      container.innerHTML = '<div class="text-muted">No notifications yet.</div>';
      return;
    }
    container.innerHTML = notifications.map(n => `
      <div class="notification-item ${n.read ? "" : "unread"}" onclick="markRead(${n.id})" style="cursor:pointer;">
        <div>${n.message}</div>
        <div class="notif-time">${WS.formatDateTime(n.createdAt)} ${n.read ? "" : "• <strong>New</strong>"}</div>
      </div>`).join("");
  } catch (e) {
    container.innerHTML = `<div class="text-danger">${e.message}</div>`;
  }
}

async function markRead(id) {
  try {
    await WS.put(`/notifications/${id}/read`, {});
    loadNotifications();
  } catch (e) {
    WS.showAlert("notifAlert", "Could not update notification: " + e.message);
  }
}

async function markAllRead() {
  try {
    const notifications = await WS.get("/notifications");
    const unread = notifications.filter(n => !n.read);
    await Promise.all(unread.map(n => WS.put(`/notifications/${n.id}/read`, {})));
    loadNotifications();
  } catch (e) {
    WS.showAlert("notifAlert", "Could not update notifications: " + e.message);
  }
}