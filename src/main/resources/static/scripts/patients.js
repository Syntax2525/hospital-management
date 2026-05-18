document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  var patients = [];
  var editingId = null;
  var tbody = document.querySelector("#patient-table tbody");
  var form = document.getElementById("patient-register-form");
  var heading = document.getElementById("reg-heading");
  var search = document.getElementById("global-search");

  function priorityClass(pr) {
    pr = (pr || "STABLE").toUpperCase();
    return pr === "HIGH" || pr === "CRITICAL" ? "hms-chip--error" : "hms-chip--success";
  }

  function findPatient(dbId) {
    return patients.find(function (p) { return String(p.dbId) === String(dbId); });
  }

  function filteredPatients() {
    var q = search ? search.value.trim().toLowerCase() : "";
    if (!q) return patients;
    return patients.filter(function (p) {
      return [p.name, p.id, p.phone, p.priority, p.status].join(" ").toLowerCase().indexOf(q) >= 0;
    });
  }

  function rowHtml(p) {
    return "<tr>" +
      "<td><strong>" + HMS.escapeHtml(p.name) + '</strong><div class="hms-muted">' + HMS.escapeHtml(p.id) + "</div></td>" +
      "<td>" + HMS.escapeHtml(p.gender) + "</td>" +
      "<td>" + HMS.escapeHtml(String(p.age || 0)) + "</td>" +
      "<td>" + HMS.escapeHtml(p.phone || "-") + "</td>" +
      '<td><span class="hms-chip ' + priorityClass(p.priority) + '">' + HMS.escapeHtml(p.priority) + "</span></td>" +
      '<td class="hms-btn-group">' +
      '<button type="button" class="hms-btn hms-btn--ghost js-view" data-id="' + p.dbId + '">View</button>' +
      '<button type="button" class="hms-btn hms-btn--secondary js-edit" data-id="' + p.dbId + '">Edit</button>' +
      '<button type="button" class="hms-btn hms-btn--primary js-consult" data-id="' + p.dbId + '">Consult</button>' +
      '<button type="button" class="hms-btn hms-btn--ghost js-bill" data-id="' + p.dbId + '">Bill</button>' +
      '<button type="button" class="hms-btn hms-btn--ghost js-lab" data-id="' + p.dbId + '">Lab</button>' +
      '<button type="button" class="hms-btn hms-btn--ghost js-delete" data-id="' + p.dbId + '">Delete</button>' +
      "</td></tr>";
  }

  function renderTable() {
    if (!tbody) return;
    var rows = filteredPatients();
    tbody.innerHTML = rows.length ? rows.map(rowHtml).join("") : '<tr><td colspan="6" class="hms-muted">No patients found.</td></tr>';
    tbody.querySelectorAll("button[data-id]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var id = btn.getAttribute("data-id");
        var p = findPatient(id);
        if (!p) return;
        if (btn.classList.contains("js-view")) {
          HMS.setSelectedPatientId(p.dbId);
          showDetailPanel(p.dbId);
        } else if (btn.classList.contains("js-edit")) {
          startEdit(p);
        } else if (btn.classList.contains("js-consult")) {
          HMS.setSelectedPatientId(p.dbId);
          HMS.navigate("consultation.html");
        } else if (btn.classList.contains("js-bill")) {
          HMS.setSelectedPatientId(p.dbId);
          HMS.setBillingContext({ patientId: p.dbId });
          HMS.navigate("billing.html");
        } else if (btn.classList.contains("js-lab")) {
          HMS.setSelectedPatientId(p.dbId);
          HMS.navigate("lab.html");
        } else if (btn.classList.contains("js-delete")) {
          confirmDelete(p);
        }
      });
    });
  }

  function showDetailPanel(id) {
    var panel = document.getElementById("patient-detail");
    if (!panel) return;
    HMS.showLoading();
    HMS.api("/api/patients/" + encodeURIComponent(id))
      .then(function (raw) {
        var p = HMS.toPatientUi(raw);
        panel.hidden = false;
        panel.querySelector("#detail-name").textContent = p.name;
        panel.querySelector("#detail-meta").textContent = p.id + " - " + p.gender + ", " + p.age + " yrs - " + (p.blood || "-");
        panel.querySelector("#detail-phone").textContent = p.phone || "-";
        panel.querySelector("#detail-allergies").textContent = p.allergies || "-";
      })
      .catch(function (err) { HMS.showToast(err.message || "Unable to load patient.", "error"); })
      .finally(HMS.hideLoading);
  }

  function startEdit(p) {
    editingId = p.dbId;
    if (heading) heading.textContent = "Edit registration";
    document.getElementById("reg-name").value = p.name || "";
    document.getElementById("reg-gender").value = p.gender || "";
    document.getElementById("reg-age").value = p.age || "";
    document.getElementById("reg-phone").value = p.phone || "";
    document.getElementById("reg-address").value = p.address || "";
    document.getElementById("reg-insurance").value = p.insurance || "";
    document.getElementById("reg-allergies").value = p.allergies || "";
    form.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  function formPayload() {
    return {
      fullName: document.getElementById("reg-name").value.trim(),
      gender: (document.getElementById("reg-gender").value || "UNKNOWN").toUpperCase(),
      age: parseInt(document.getElementById("reg-age").value, 10) || 0,
      phone: document.getElementById("reg-phone").value.trim(),
      address: document.getElementById("reg-address").value.trim(),
      insuranceProvider: document.getElementById("reg-insurance").value.trim(),
      allergies: document.getElementById("reg-allergies").value.trim(),
    };
  }

  function resetForm() {
    editingId = null;
    if (heading) heading.textContent = "New registration";
    if (form) form.reset();
  }

  function loadPatients() {
    HMS.showLoading();
    return HMS.loadPatients()
      .then(function (list) {
        patients = list;
        renderTable();
      })
      .catch(function (err) {
        HMS.showToast(err.message || "Could not load patients from the database.", "error");
      })
      .finally(HMS.hideLoading);
  }

  function confirmDelete(p) {
    HMS.openModal({
      title: "Delete patient",
      bodyHtml: "<p>Delete <strong>" + HMS.escapeHtml(p.name) + "</strong> from the database?</p>",
      actions: [
        {
          label: "Delete",
          primary: true,
          onClick: function () {
            HMS.showLoading();
            HMS.api("/api/patients/" + p.dbId, { method: "DELETE" })
              .then(function () {
                HMS.showToast("Patient deleted.", "success");
                if (HMS.getSelectedPatientId() === p.dbId) HMS.clearSelectedPatient();
                return loadPatients();
              })
              .catch(function (err) { HMS.showToast(err.message || "Delete failed.", "error"); })
              .finally(HMS.hideLoading);
          },
        },
        { label: "Cancel", primary: false },
      ],
    });
  }

  if (search) search.addEventListener("input", renderTable);
  loadPatients();

  var toConsult = document.getElementById("detail-open-consult");
  if (toConsult) toConsult.addEventListener("click", function () {
    if (HMS.getSelectedPatientId()) HMS.navigate("consultation.html");
  });
  var toLab = document.getElementById("detail-view-lab");
  if (toLab) toLab.addEventListener("click", function () {
    if (HMS.getSelectedPatientId()) HMS.navigate("lab.html");
  });

  if (form) {
    form.addEventListener("submit", function (e) {
      e.preventDefault();
      var payload = formPayload();
      if (!payload.fullName) {
        HMS.showToast("Full name is required.", "error");
        return;
      }
      HMS.showLoading();
      var path = editingId ? "/api/patients/" + editingId : "/api/patients";
      var method = editingId ? "PUT" : "POST";
      HMS.api(path, { method: method, body: JSON.stringify(payload) })
        .then(function (saved) {
          HMS.showToast((editingId ? "Patient updated: " : "Patient registered: ") + saved.patientNumber, "success");
          resetForm();
          return loadPatients();
        })
        .catch(function (err) {
          HMS.showToast(err.message || "Patient save failed.", "error");
        })
        .finally(HMS.hideLoading);
    });
  }
});
