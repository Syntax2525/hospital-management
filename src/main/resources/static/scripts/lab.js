document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  var list = document.getElementById("lab-request-list");
  var patientLine = document.getElementById("lab-patient-line");

  function renderOrders(orders) {
    if (!list) return;
    if (!orders.length) {
      list.innerHTML = '<li class="hms-muted">No lab orders in the database.</li>';
      return;
    }
    list.innerHTML = orders.map(function (o) {
      return "<li><span><strong>" + HMS.escapeHtml(o.orderNumber) + "</strong> - " +
        HMS.escapeHtml(o.patientName) + " - " + HMS.escapeHtml(o.status) + "<br><span class=\"hms-muted\">" +
        HMS.escapeHtml((o.tests || []).join(", ")) + "</span></span>" +
        '<button type="button" class="hms-btn hms-btn--secondary js-result" data-id="' + o.id + '">Publish result</button></li>';
    }).join("");
    list.querySelectorAll(".js-result").forEach(function (btn) {
      btn.addEventListener("click", function () {
        HMS.showToast("Open a specific lab item from the order details to publish a result.", "info");
      });
    });
  }

  function load() {
    HMS.showLoading();
    Promise.all([HMS.api("/api/clinical/lab-orders"), HMS.getSelectedPatient()])
      .then(function (results) {
        var orders = results[0] || [];
        var patient = results[1];
        if (patientLine) patientLine.textContent = patient ? patient.name + " (" + patient.id + ")" : "All database lab orders";
        renderOrders(orders);
        var values = document.querySelectorAll(".hms-grid--stats .hms-stat__value");
        if (values[0]) values[0].textContent = orders.filter(function (o) { return o.status === "REQUESTED" || o.status === "IN_PROGRESS"; }).length;
        if (values[1]) values[1].textContent = orders.filter(function (o) { return o.status === "RESULT_READY" || o.status === "VERIFIED"; }).length;
        if (values[2]) values[2].textContent = orders.filter(function (o) { return (o.tests || []).join(" ").toUpperCase().indexOf("STAT") >= 0; }).length;
      })
      .catch(function (err) { HMS.showToast(err.message || "Unable to load lab orders.", "error"); })
      .finally(HMS.hideLoading);
  }

  var refresh = document.getElementById("lab-refresh");
  if (refresh) refresh.addEventListener("click", load);
  load();
});
