document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  function setStats(stats) {
    var values = document.querySelectorAll(".hms-grid--stats .hms-stat__value");
    if (values[0]) values[0].textContent = stats.totalPatients;
    if (values[1]) values[1].textContent = stats.appointmentsToday;
    if (values[2]) values[2].textContent = stats.availableBeds;
    if (values[3]) values[3].textContent = "$" + Number(stats.monthToDateRevenue || 0).toFixed(2);
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
  Promise.all([
    HMS.api("/api/dashboard/stats"),
    HMS.api("/api/notifications"),
  ]).then(function (results) {
    setStats(results[0]);
    setActivities({ notifications: results[1] || [] });
  }).catch(function (err) {
    HMS.showToast(err.message || "Dashboard data could not be loaded.", "error");
  }).finally(HMS.hideLoading);

  var regBtn = document.getElementById("qa-register");
  if (regBtn) regBtn.addEventListener("click", function () { HMS.navigate("patients.html"); });

  var apptBtn = document.getElementById("qa-appointment");
  if (apptBtn) apptBtn.addEventListener("click", function () { HMS.navigate("patients.html"); });

  var labBtn = document.getElementById("qa-lab");
  if (labBtn) labBtn.addEventListener("click", function () { HMS.navigate("lab.html"); });

  var alertBtn = document.getElementById("card-emergency");
  if (alertBtn) alertBtn.addEventListener("click", function () { HMS.navigate("triage.html"); });

  var viewAll = document.getElementById("activity-view-all");
  if (viewAll) viewAll.addEventListener("click", function () { HMS.navigate("reports.html"); });
});
