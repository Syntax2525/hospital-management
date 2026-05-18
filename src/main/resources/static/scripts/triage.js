document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  var patient = null;

  function renderPatient(p) {
    var nameEl = document.getElementById("triage-patient-name");
    var metaEl = document.getElementById("triage-patient-meta");
    var bloodEl = document.getElementById("triage-blood");
    var visitEl = document.getElementById("triage-last-visit");
    var allergiesEl = document.getElementById("triage-allergies");
    var prEl = document.getElementById("triage-priority");
    if (!p) return;
    if (nameEl) nameEl.textContent = p.name;
    if (metaEl) metaEl.textContent = p.id + " - " + p.gender + ", " + p.age + " yrs";
    if (bloodEl) bloodEl.textContent = p.blood || "-";
    if (visitEl) visitEl.textContent = p.lastVisit || "-";
    if (prEl) {
      prEl.textContent = (p.priority || "STABLE").toUpperCase();
      prEl.className = "hms-chip " + ((p.priority || "").toUpperCase() === "HIGH" ? "hms-chip--error" : "hms-chip--success");
    }
    if (allergiesEl) {
      allergiesEl.innerHTML = (p.allergies || "None").split(",").map(function (a) {
        return '<span class="hms-chip hms-chip--warn">' + HMS.escapeHtml(a.trim()) + "</span>";
      }).join(" ");
    }
  }

  HMS.getSelectedPatient().then(function (p) {
    patient = p;
    if (p) renderPatient(p);
    else HMS.showToast("Select a patient from Patients before saving triage.", "error", 6000);
  });

  var clearBtn = document.getElementById("triage-clear");
  if (clearBtn) clearBtn.addEventListener("click", function () {
    document.querySelectorAll("#triage-form input, #triage-form textarea").forEach(function (el) { el.value = ""; });
    HMS.showToast("Form cleared.", "info");
  });

  var saveBtn = document.getElementById("triage-save");
  if (saveBtn) saveBtn.addEventListener("click", function () {
    if (!patient) {
      HMS.showToast("Select a patient before saving triage.", "error");
      return;
    }
    HMS.showLoading();
    HMS.api("/api/clinical/triage", {
      method: "POST",
      body: JSON.stringify({
        patientId: patient.dbId,
        chiefComplaint: document.getElementById("v-complaint").value || "Triage assessment",
        category: "NOT_ASSIGNED",
        priority: (patient.priority || "STABLE").toUpperCase(),
        vitalSign: {
          bloodPressure: document.getElementById("v-bp").value,
          temperatureCelsius: document.getElementById("v-temp").value ? Number(document.getElementById("v-temp").value) : null,
          pulseBpm: document.getElementById("v-pulse").value ? Number(document.getElementById("v-pulse").value) : null,
          oxygenSaturation: document.getElementById("v-spo2").value ? Number(document.getElementById("v-spo2").value) : null,
          weightKg: document.getElementById("v-weight").value ? Number(document.getElementById("v-weight").value) : null,
          heightCm: document.getElementById("v-height").value ? Number(document.getElementById("v-height").value) : null,
        },
      }),
    }).then(function () {
      HMS.showToast("Triage record saved to the database.", "success");
      return HMS.api("/api/patients/" + patient.dbId).then(HMS.toPatientUi);
    }).then(function (fresh) {
      patient = fresh;
      renderPatient(fresh);
    }).catch(function (err) {
      HMS.showToast(err.message || "Unable to save triage.", "error");
    }).finally(HMS.hideLoading);
  });

  var ecg = document.getElementById("triage-ecg");
  if (ecg) ecg.addEventListener("click", function () {
    if (!patient) {
      HMS.showToast("Select a patient before requesting ECG.", "error");
      return;
    }
    HMS.showLoading();
    HMS.api("/api/clinical/lab-orders", {
      method: "POST",
      body: JSON.stringify({ patientId: patient.dbId, notes: "Requested from triage", tests: ["ECG"] }),
    }).then(function () {
      HMS.showToast("ECG lab order saved.", "success");
    }).catch(function (err) {
      HMS.showToast(err.message || "Unable to request ECG.", "error");
    }).finally(HMS.hideLoading);
  });
});
