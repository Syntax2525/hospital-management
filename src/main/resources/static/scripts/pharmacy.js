document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  var tbody = document.querySelector(".hms-table tbody");

  function renderStats(meds, queue) {
    var values = document.querySelectorAll(".hms-grid--stats .hms-stat__value");
    if (values[0]) values[0].textContent = queue.length;
    if (values[1]) values[1].textContent = meds.filter(function (m) { return m.inventoryStatus === "LOW_STOCK" || m.inventoryStatus === "OUT_OF_STOCK"; }).length;
    if (values[2]) values[2].textContent = meds.filter(function (m) { return m.inventoryStatus === "IN_STOCK"; }).length;
  }

  function renderQueue(queue) {
    if (!tbody) return;
    if (!queue.length) {
      tbody.innerHTML = '<tr><td colspan="4" class="hms-muted">No pending prescription items in the database.</td></tr>';
      return;
    }
    tbody.innerHTML = queue.map(function (item) {
      return "<tr><td>" + HMS.escapeHtml(item.prescriptionNumber) + "</td><td>" + HMS.escapeHtml(item.patientName) +
        "</td><td>" + HMS.escapeHtml(item.medicationName + " " + (item.strength || "")) +
        '</td><td><button type="button" class="hms-btn hms-btn--primary js-dispense" data-id="' + item.prescriptionItemId +
        '" data-qty="' + (item.quantity || 1) + '">Dispense</button></td></tr>';
    }).join("");
    tbody.querySelectorAll(".js-dispense").forEach(function (btn) {
      btn.addEventListener("click", function () {
        HMS.showLoading();
        HMS.api("/api/pharmacy/dispenses", {
          method: "POST",
          body: JSON.stringify({ prescriptionItemId: Number(btn.dataset.id), quantityDispensed: Number(btn.dataset.qty || 1) }),
        }).then(function () {
          HMS.showToast("Dispense recorded in the database.", "success");
          return load();
        }).catch(function (err) {
          HMS.showToast(err.message || "Dispense failed.", "error");
        }).finally(HMS.hideLoading);
      });
    });
  }

  function load() {
    return Promise.all([HMS.api("/api/pharmacy/medications"), HMS.api("/api/pharmacy/prescriptions")])
      .then(function (results) {
        renderStats(results[0] || [], results[1] || []);
        renderQueue(results[1] || []);
      })
      .catch(function (err) { HMS.showToast(err.message || "Unable to load pharmacy data.", "error"); });
  }

  HMS.showLoading();
  load().finally(HMS.hideLoading);
});
