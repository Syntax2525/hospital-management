document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  function render(summary) {
    var cards = document.querySelectorAll(".hms-report-grid .hms-card");
    if (cards[0]) {
      cards[0].querySelector(".hms-title").textContent = "Operational volume";
      cards[0].querySelector(".hms-bar-chart").setAttribute("aria-label", "Database-backed operational volume");
    }
    if (cards[1]) {
      cards[1].querySelector(".hms-title").textContent = "Revenue";
      cards[1].querySelector(".hms-muted").textContent = summary.invoices + " invoices in database";
      cards[1].querySelector(".hms-stat__value").textContent = "$" + Number(summary.revenue || 0).toFixed(2);
    }
    if (cards[2]) {
      cards[2].querySelector(".hms-title").textContent = "Capacity";
      cards[2].querySelector(".hms-stat__value").textContent = summary.availableBeds + " beds";
      cards[2].querySelector(".hms-muted").textContent = summary.occupiedBeds + " occupied, " + summary.lowStockMedications + " low-stock medications";
    }
  }

  HMS.showLoading();
  HMS.api("/api/reports/summary")
    .then(render)
    .catch(function (err) { HMS.showToast(err.message || "Unable to load reports.", "error"); })
    .finally(HMS.hideLoading);

  document.querySelectorAll(".hms-segment button").forEach(function (btn) {
    btn.addEventListener("click", function () {
      document.querySelectorAll(".hms-segment button").forEach(function (b) { b.classList.remove("is-active"); });
      btn.classList.add("is-active");
      HMS.api("/api/reports/summary").then(render);
    });
  });

  var pdf = document.getElementById("report-export-pdf");
  if (pdf) pdf.addEventListener("click", function () {
    window.print();
  });
});
