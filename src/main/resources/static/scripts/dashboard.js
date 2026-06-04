document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  var role = String((HMS.getSession() && HMS.getSession().role) || "").toUpperCase();

  var DASHBOARD_CONFIG = {
    ADMIN: {
      stats: [
        ["Total patients", function (s) { return s.totalPatients; }],
        ["Appointments today", function (s) { return s.appointmentsToday; }],
        ["Available beds", function (s) { return s.availableBeds; }],
        ["Revenue (MTD)", function (s) { return "$" + Number(s.monthToDateRevenue || 0).toFixed(2); }],
      ],
      actions: [
        ["dashboard", "Monitor workflow", "dashboard.html"],
        ["bar_chart", "Open reports", "reports.html"],
        ["manage_accounts", "Manage users", "users.html"],
      ],
    },
    RECEPTIONIST: {
      stats: [
        ["Registered patients", function (s) { return s.totalPatients; }],
        ["Appointments today", function (s) { return s.appointmentsToday; }],
      ],
      actions: [
        ["person_add", "Register patient", "patients.html"],
        ["add_task", "Book appointment", "patients.html"],
      ],
    },
    DOCTOR: {
      stats: [
        ["Consultations & lab reviews", function (s) { return s.totalPatients; }],
        ["Appointments today", function (s) { return s.appointmentsToday; }],
        ["Lab results to review", function (s) { return s.pendingLabTests; }],
      ],
      actions: [
        ["stethoscope", "Open consultation", "consultation.html"],
        ["microscope", "Review lab results", "lab.html"],
        ["person_search", "Assigned patients", "patients.html"],
      ],
    },
    NURSE: {
      stats: [
        ["Waiting assessment", function (s) { return s.totalPatients; }],
        ["Available beds", function (s) { return s.availableBeds; }],
      ],
      actions: [
        ["medical_information", "Record vitals", "triage.html"],
        ["person_search", "Monitor patients", "patients.html"],
      ],
    },
    LAB_TECHNICIAN: {
      stats: [
        ["Pending tests", function (s) { return s.pendingLabTests; }],
      ],
      actions: [
        ["biotech", "Open lab requests", "lab.html"],
        ["fact_check", "Enter results", "lab.html"],
      ],
    },
    PHARMACIST: {
      stats: [
        ["Pending prescriptions", function (s) { return s.lowStockAlerts; }],
      ],
      actions: [
        ["local_pharmacy", "View prescriptions", "pharmacy.html"],
        ["inventory_2", "Manage inventory", "pharmacy.html"],
      ],
    },
    BILLING: {
      stats: [
        ["Revenue (MTD)", function (s) { return "$" + Number(s.monthToDateRevenue || 0).toFixed(2); }],
        ["Ready for billing", function (s) { return s.totalPatients; }],
      ],
      actions: [
        ["receipt_long", "Generate invoice", "billing.html"],
        ["payments", "Record payment", "billing.html"],
      ],
    },
  };

  function config() {
    return DASHBOARD_CONFIG[role] || DASHBOARD_CONFIG.ADMIN;
  }

  function setStats(stats) {
    var cards = document.querySelectorAll(".hms-grid--stats .hms-card");
    var rows = config().stats;
    cards.forEach(function (card, index) {
      var row = rows[index];
      card.style.display = row ? "" : "none";
      if (!row) return;
      var value = card.querySelector(".hms-stat__value");
      var label = card.querySelector(".hms-stat__label");
      if (value) value.textContent = row[1](stats || {});
      if (label) label.textContent = row[0];
    });
  }

  function setQuickActions() {
    var buttons = [document.getElementById("qa-register"), document.getElementById("qa-appointment"), document.getElementById("qa-lab")];
    config().actions.forEach(function (action, index) {
      var btn = buttons[index];
      if (!btn) return;
      btn.style.display = "";
      btn.innerHTML = '<span class="material-symbols-outlined">' + action[0] + "</span> " + HMS.escapeHtml(action[1]);
      btn.onclick = function () { HMS.navigate(action[2]); };
    });
    buttons.slice(config().actions.length).forEach(function (btn) {
      if (btn) btn.style.display = "none";
    });
  }

  function setActivities(data) {
    var host = document.querySelector(".hms-activity");
    if (!host) return;
    var rows = [];
    (data.notifications || []).slice(0, 3).forEach(function (n) {
      rows.push("<div class=\"hms-activity__row\"><div><p><strong>" + HMS.escapeHtml(n.title) +
        "</strong></p><p class=\"hms-muted\">" + HMS.escapeHtml(n.message) + "</p></div><span class=\"hms-muted\">" +
        HMS.escapeHtml((n.createdAt || "").replace("T", " ").slice(0, 16)) + "</span></div>");
    });
    if (!rows.length) rows.push('<div class="hms-activity__row"><p class="hms-muted">No database activity yet.</p></div>');
    host.innerHTML = rows.join("");
  }

  HMS.showLoading();
  setQuickActions();
  Promise.all([
    HMS.api("/api/dashboard/stats"),
    HMS.api("/api/notifications"),
  ]).then(function (results) {
    setStats(results[0]);
    setActivities({ notifications: results[1] || [] });
  }).catch(function (err) {
    HMS.showToast(err.message || "Dashboard data could not be loaded.", "error");
  }).finally(HMS.hideLoading);

  var alertBtn = document.getElementById("card-emergency");
  if (alertBtn) {
    if (role === "NURSE") {
      alertBtn.addEventListener("click", function () { HMS.navigate("triage.html"); });
    } else {
      alertBtn.style.display = "none";
    }
  }

  var viewAll = document.getElementById("activity-view-all");
  if (viewAll) {
    if (role === "ADMIN") viewAll.addEventListener("click", function () { HMS.navigate("reports.html"); });
    else viewAll.style.display = "none";
  }
});
