document.addEventListener("DOMContentLoaded", function () {
  if (!window.HMS) return;

  var users = [];
  var tbody = document.querySelector(".hms-table tbody");
  var search = document.getElementById("global-search");

  function filtered() {
    var q = search ? search.value.trim().toLowerCase() : "";
    if (!q) return users;
    return users.filter(function (u) {
      return [u.fullName, u.email, u.role, u.status, u.departmentName].join(" ").toLowerCase().indexOf(q) >= 0;
    });
  }

  function render() {
    var rows = filtered();
    var values = document.querySelectorAll(".hms-grid--stats .hms-stat__value");
    if (values[0]) values[0].textContent = users.filter(function (u) { return u.status === "ACTIVE"; }).length;
    if (values[1]) values[1].textContent = users.filter(function (u) { return u.status === "PENDING_INVITE"; }).length;
    if (values[2]) values[2].textContent = new Set(users.map(function (u) { return u.role; })).size;
    if (!tbody) return;
    tbody.innerHTML = rows.length ? rows.map(function (u) {
      return "<tr><td><strong>" + HMS.escapeHtml(u.fullName) + '</strong><div class="hms-muted">' + HMS.escapeHtml(u.email) +
        '</div></td><td><span class="hms-role-pill">' + HMS.escapeHtml(u.role) + "</span></td><td>" +
        HMS.escapeHtml(u.departmentName || "-") + '</td><td><button type="button" class="hms-btn hms-btn--ghost js-delete" data-id="' +
        u.id + '">Delete</button></td></tr>';
    }).join("") : '<tr><td colspan="4" class="hms-muted">No users found.</td></tr>';
    tbody.querySelectorAll(".js-delete").forEach(function (btn) {
      btn.addEventListener("click", function () {
        HMS.showLoading();
        HMS.api("/api/users/" + btn.dataset.id, { method: "DELETE" })
          .then(function () { HMS.showToast("User deleted.", "success"); return load(); })
          .catch(function (err) { HMS.showToast(err.message || "Delete failed.", "error"); })
          .finally(HMS.hideLoading);
      });
    });
  }

  function load() {
    return HMS.api("/api/users").then(function (list) {
      users = list || [];
      render();
    }).catch(function (err) {
      HMS.showToast(err.message || "Unable to load users.", "error");
    });
  }

  if (search) search.addEventListener("input", render);

  var add = document.getElementById("user-add-btn");
  if (add) add.addEventListener("click", function () {
    HMS.openModal({
      title: "Add user",
      bodyHtml:
        '<div class="hms-field"><label for="nu-name">Full name</label><input class="hms-input" id="nu-name" type="text" /></div>' +
        '<div class="hms-field"><label for="nu-email">Email</label><input class="hms-input" id="nu-email" type="email" /></div>' +
        '<div class="hms-field"><label for="nu-password">Temporary password</label><input class="hms-input" id="nu-password" type="password" value="ChangeMe123" /></div>' +
        '<div class="hms-field"><label for="nu-role">Role</label><select class="hms-select" id="nu-role"><option>ADMIN</option><option>DOCTOR</option><option>NURSE</option><option>LAB_TECHNICIAN</option><option>PHARMACIST</option><option>BILLING</option><option>RECEPTIONIST</option></select></div>',
      actions: [{
        label: "Create",
        primary: true,
        onClick: function () {
          HMS.showLoading();
          HMS.api("/api/users", {
            method: "POST",
            body: JSON.stringify({
              fullName: document.getElementById("nu-name").value.trim(),
              email: document.getElementById("nu-email").value.trim(),
              temporaryPassword: document.getElementById("nu-password").value,
              role: document.getElementById("nu-role").value,
              phone: "",
            }),
          }).then(function () {
            HMS.showToast("User created.", "success");
            return load();
          }).catch(function (err) {
            HMS.showToast(err.message || "User creation failed.", "error");
          }).finally(HMS.hideLoading);
        },
      }, { label: "Cancel", primary: false }],
    });
  });

  HMS.showLoading();
  load().finally(HMS.hideLoading);
});
