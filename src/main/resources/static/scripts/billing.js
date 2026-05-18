document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  var ctx = HMS.getBillingContext();
  var patientId = ctx && ctx.patientId ? ctx.patientId : HMS.getSelectedPatientId();
  var nameEl = document.getElementById("bill-patient-name");
  var idEl = document.getElementById("bill-patient-id");
  var lines = document.getElementById("bill-lines");
  var totalEl = document.getElementById("bill-total");
  var currentPatient = null;

  function renderInvoices(invoices) {
    var patientInvoices = currentPatient ? invoices.filter(function (i) { return i.patientNumber === currentPatient.id; }) : invoices;
    if (!lines) return;
    if (!patientInvoices.length) {
      lines.innerHTML = '<p class="hms-muted">No invoices found in the database for this context.</p>';
      if (totalEl) totalEl.textContent = "$0.00";
      return;
    }
    lines.innerHTML = patientInvoices.slice(0, 8).map(function (i) {
      return '<div class="hms-invoice-line"><span>' + HMS.escapeHtml(i.invoiceNumber) + " - " +
        HMS.escapeHtml(i.patientName) + " (" + HMS.escapeHtml(i.status) + ')</span><span>$' +
        Number(i.totalAmount || 0).toFixed(2) + "</span></div>";
    }).join("");
    var sum = patientInvoices.reduce(function (a, b) { return a + Number(b.totalAmount || 0); }, 0);
    if (totalEl) totalEl.textContent = "$" + sum.toFixed(2);
  }

  function load() {
    HMS.showLoading();
    Promise.all([
      patientId ? HMS.api("/api/patients/" + patientId).then(HMS.toPatientUi) : Promise.resolve(null),
      HMS.api("/api/billing/invoices"),
    ]).then(function (results) {
      currentPatient = results[0];
      if (nameEl) nameEl.textContent = currentPatient ? currentPatient.name : "All patients";
      if (idEl) idEl.textContent = currentPatient ? currentPatient.id : "Database invoices";
      renderInvoices(results[1] || []);
    }).catch(function (err) {
      HMS.showToast(err.message || "Unable to load billing data.", "error");
    }).finally(HMS.hideLoading);
  }

  var gen = document.getElementById("bill-generate");
  if (gen) gen.addEventListener("click", function () {
    if (!currentPatient) {
      HMS.showToast("Select a patient before creating an invoice.", "error");
      return;
    }
    HMS.openModal({
      title: "Create invoice",
      bodyHtml:
        '<div class="hms-field"><label for="inv-label">Line item</label><input class="hms-input" id="inv-label" value="Consultation fee" /></div>' +
        '<div class="hms-field"><label for="inv-amount">Amount</label><input class="hms-input" id="inv-amount" type="number" min="0" step="0.01" value="120" /></div>',
      actions: [{
        label: "Save invoice",
        primary: true,
        onClick: function () {
          var label = document.getElementById("inv-label").value.trim();
          var amount = Number(document.getElementById("inv-amount").value || 0);
          HMS.showLoading();
          HMS.api("/api/billing/invoices", {
            method: "POST",
            body: JSON.stringify({
              patientId: currentPatient.dbId,
              items: [{ label: label, quantity: 1, unitAmount: amount, lineTotal: amount }],
            }),
          }).then(function (invoice) {
            HMS.showToast("Invoice created: " + invoice.invoiceNumber, "success");
            return load();
          }).catch(function (err) {
            HMS.showToast(err.message || "Invoice creation failed.", "error");
          }).finally(HMS.hideLoading);
        },
      }, { label: "Cancel", primary: false }],
    });
  });

  load();
});
