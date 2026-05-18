document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  var patient = null;
  var title = document.getElementById("consult-patient-name");
  var meta = document.getElementById("consult-patient-meta");

  function renderPatient(p) {
    if (!p) {
      if (title) title.textContent = "No patient selected";
      if (meta) meta.textContent = "Choose a patient from the Patients page.";
      return;
    }
    if (title) title.textContent = p.name;
    if (meta) meta.innerHTML =
      '<span class="hms-pid">PID: ' + HMS.escapeHtml(p.id) + '</span><span class="hms-pid">Age: ' +
      HMS.escapeHtml(p.age) + " / " + HMS.escapeHtml(p.gender) + '</span><span class="hms-pid">Blood: ' +
      HMS.escapeHtml(p.blood || "-") + "</span>";
  }

  HMS.getSelectedPatient().then(function (p) {
    patient = p;
    renderPatient(p);
    if (!p) HMS.showToast("Select a patient from Patients to begin.", "error", 6000);
  }).catch(function (err) {
    HMS.showToast(err.message || "Unable to load selected patient.", "error");
  });

  var saveBtn = document.getElementById("consult-save");
  if (saveBtn) saveBtn.addEventListener("click", function () {
    if (!patient) {
      HMS.showToast("Select a patient before saving a consultation.", "error");
      return;
    }
    var tests = [];
    document.querySelectorAll(".lab-check:checked").forEach(function (cb) { tests.push(cb.value); });
    var notesEl = document.getElementById("clinical-notes");
    HMS.showLoading();
    HMS.api("/api/clinical/consultations", {
      method: "POST",
      body: JSON.stringify({
        patientId: patient.dbId,
        clinicalNotes: notesEl ? notesEl.value : "",
        treatmentPlan: "",
        diagnosisCodes: ["I10"],
        requestedLabTests: tests,
        prescriptionItems: [],
      }),
    }).then(function () {
      HMS.showToast("Consultation saved to the database.", "success");
    }).catch(function (err) {
      HMS.showToast(err.message || "Unable to save consultation.", "error");
    }).finally(HMS.hideLoading);
  });

  var labNav = document.getElementById("consult-open-lab");
  if (labNav) labNav.addEventListener("click", function () { HMS.navigate("lab.html"); });
});
