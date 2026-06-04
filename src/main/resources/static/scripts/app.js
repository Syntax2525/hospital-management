(function (global) {
  "use strict";

  var KEYS = {
    session: "hms:session",
    selectedPatientId: "hms:selectedPatientId",
    billingContext: "hms:billingContext",
  };

  var NAV = [
    { id: "dashboard", href: "dashboard.html", icon: "dashboard", label: "Dashboard" },
    { id: "patients", href: "patients.html", icon: "person_search", label: "Patients" },
    { id: "triage", href: "triage.html", icon: "medical_information", label: "Triage" },
    { id: "consultation", href: "consultation.html", icon: "stethoscope", label: "Consultation" },
    { id: "lab", href: "lab.html", icon: "biotech", label: "Laboratory" },
    { id: "pharmacy", href: "pharmacy.html", icon: "local_pharmacy", label: "Pharmacy" },
    { id: "billing", href: "billing.html", icon: "payments", label: "Billing" },
    { id: "reports", href: "reports.html", icon: "bar_chart", label: "Reports" },
    { id: "users", href: "users.html", icon: "manage_accounts", label: "User Management" },
  ];

  var ROLE_PAGES = {
    ADMIN: ["dashboard", "reports", "users"],
    RECEPTIONIST: ["dashboard", "patients"],
    DOCTOR: ["dashboard", "patients", "consultation", "lab"],
    NURSE: ["dashboard", "patients", "triage"],
    LAB_TECHNICIAN: ["dashboard", "lab"],
    PHARMACIST: ["dashboard", "pharmacy"],
    BILLING: ["dashboard", "billing"],
  };

  var ROLE_DASHBOARD_TITLE = {
    ADMIN: "Admin Dashboard",
    RECEPTIONIST: "Receptionist Dashboard",
    DOCTOR: "Doctor Dashboard",
    NURSE: "Nurse Dashboard",
    LAB_TECHNICIAN: "Laboratory Dashboard",
    PHARMACIST: "Pharmacy Dashboard",
    BILLING: "Billing Dashboard",
  };

  var ROLE_DASHBOARD_URL = {
    ADMIN: "dashboard.html?role=admin",
    RECEPTIONIST: "dashboard.html?role=receptionist",
    DOCTOR: "dashboard.html?role=doctor",
    NURSE: "dashboard.html?role=nurse",
    LAB_TECHNICIAN: "dashboard.html?role=laboratory",
    PHARMACIST: "dashboard.html?role=pharmacy",
    BILLING: "dashboard.html?role=billing",
  };

  var DOC_TITLES = {
    dashboard: "Dashboard - MediFlow HMS",
    patients: "Patients - MediFlow HMS",
    triage: "Triage & Vitals - MediFlow HMS",
    consultation: "Consultation - MediFlow HMS",
    lab: "Laboratory - MediFlow HMS",
    pharmacy: "Pharmacy - MediFlow HMS",
    billing: "Billing - MediFlow HMS",
    reports: "Reports - MediFlow HMS",
    users: "User Management - MediFlow HMS",
  };

  var HEADINGS = {
    dashboard: "Dashboard",
    patients: "Patients",
    triage: "Triage & Vitals",
    consultation: "Consultation",
    lab: "Laboratory",
    pharmacy: "Pharmacy",
    billing: "Billing",
    reports: "Reports & Analytics",
    users: "User Management",
  };

  function readJson(key, fallback) {
    try {
      var raw = localStorage.getItem(key);
      return raw ? JSON.parse(raw) : fallback;
    } catch (e) {
      return fallback;
    }
  }

  function writeJson(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  function getSession() {
    return readJson(KEYS.session, null);
  }

  function authHeaders() {
    var session = getSession();
    var headers = { "Content-Type": "application/json" };
    if (session && session.token) headers.Authorization = "Bearer " + session.token;
    return headers;
  }

  function api(path, options) {
    if (document.body && document.body.getAttribute("data-access-denied") === "true") {
      return Promise.reject(new Error("Access Denied"));
    }
    options = options || {};
    options.headers = Object.assign(authHeaders(), options.headers || {});
    return fetch(path, options).then(function (res) {
      if (res.status === 204) return null;
      return res.json().catch(function () {
        return { success: res.ok, message: res.statusText };
      }).then(function (body) {
        if (!res.ok || body.success === false) {
          var err = new Error(body.message || "Request failed.");
          err.status = res.status;
          if (res.status === 403) showAccessDenied(body.message || "Access Denied");
          throw err;
        }
        return body.data;
      });
    });
  }

  function escapeHtml(s) {
    return String(s == null ? "" : s)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function toPatientUi(p) {
    if (!p) return null;
    var allergies = Array.isArray(p.allergies) ? p.allergies.join(", ") : p.allergies;
    return {
      dbId: p.id,
      id: p.patientNumber,
      name: p.fullName,
      gender: p.gender || "UNKNOWN",
      age: p.age || 0,
      blood: (p.bloodGroup || "UNKNOWN").replace("_POSITIVE", "+").replace("_NEGATIVE", "-"),
      phone: p.phone || "",
      address: p.address || "",
      insurance: p.insuranceProvider || "",
      allergies: allergies || "None recorded",
      lastVisit: p.lastVisitDate || "",
      priority: p.priority || "STABLE",
      status: p.status || "ACTIVE",
    };
  }

  function loadPatients() {
    return api("/api/patients").then(function (list) {
      return (list || []).map(toPatientUi);
    });
  }

  function getSelectedPatientId() {
    var raw = localStorage.getItem(KEYS.selectedPatientId);
    return raw ? Number(raw) : null;
  }

  function setSelectedPatientId(id) {
    if (id == null) localStorage.removeItem(KEYS.selectedPatientId);
    else localStorage.setItem(KEYS.selectedPatientId, String(id));
  }

  function getSelectedPatient() {
    var id = getSelectedPatientId();
    if (!id) return Promise.resolve(null);
    return api("/api/patients/" + encodeURIComponent(id)).then(toPatientUi);
  }

  function requireAuth() {
    var s = getSession();
    if (!s || !s.loggedIn) {
      window.location.href = "index.html";
      return false;
    }
    return true;
  }

  function normalizeRole(role) {
    return String(role || "").toUpperCase();
  }

  function dashboardForRole(role) {
    return ROLE_DASHBOARD_URL[normalizeRole(role)] || "dashboard.html";
  }

  function hasPageAccess(page) {
    var session = getSession();
    var role = normalizeRole(session && session.role);
    return (ROLE_PAGES[role] || []).indexOf(page) >= 0;
  }

  function showAccessDenied(message) {
    ensureHosts();
    if (document.body) document.body.setAttribute("data-access-denied", "true");
    showToast(message || "Access Denied", "error", 6000);
    var main = document.getElementById("main");
    if (main) {
      main.innerHTML =
        '<section class="hms-card" role="alert">' +
        '<h2 class="hms-title">Access Denied</h2>' +
        '<p class="hms-muted">You do not have permission to access this feature.</p>' +
        '<button type="button" class="hms-btn hms-btn--primary" id="hms-denied-home">Go to dashboard</button>' +
        '</section>';
      var btn = document.getElementById("hms-denied-home");
      if (btn) btn.addEventListener("click", function () {
        var s = getSession();
        window.location.href = dashboardForRole(s && s.role);
      });
    }
  }

  function ensureHosts() {
    if (!document.getElementById("hms-toast-host")) {
      var t = document.createElement("div");
      t.id = "hms-toast-host";
      document.body.appendChild(t);
    }
    if (!document.getElementById("hms-loading")) {
      var l = document.createElement("div");
      l.id = "hms-loading";
      l.innerHTML = '<div class="hms-spinner" role="status" aria-label="Loading"></div>';
      document.body.appendChild(l);
    }
    if (!document.getElementById("hms-modal-overlay")) {
      var m = document.createElement("div");
      m.id = "hms-modal-overlay";
      m.className = "hms-modal-overlay";
      m.setAttribute("role", "dialog");
      m.setAttribute("aria-modal", "true");
      m.innerHTML =
        '<div class="hms-modal" id="hms-modal-box">' +
        '<div class="hms-modal__header"><h2 class="hms-title" id="hms-modal-title"></h2>' +
        '<button type="button" class="hms-icon-btn" id="hms-modal-close" aria-label="Close">' +
        '<span class="material-symbols-outlined">close</span></button></div>' +
        '<div class="hms-modal__body" id="hms-modal-body"></div>' +
        '<div class="hms-modal__footer" id="hms-modal-footer"></div></div>';
      document.body.appendChild(m);
      m.addEventListener("click", function (e) {
        if (e.target === m) closeModal();
      });
      document.getElementById("hms-modal-close").addEventListener("click", closeModal);
    }
  }

  function showLoading() {
    var el = document.getElementById("hms-loading");
    if (el) el.classList.add("is-visible");
  }

  function hideLoading() {
    var el = document.getElementById("hms-loading");
    if (el) el.classList.remove("is-visible");
  }

  function showToast(message, type, ms) {
    ensureHosts();
    type = type || "info";
    ms = ms == null ? 3500 : ms;
    var host = document.getElementById("hms-toast-host");
    var div = document.createElement("div");
    div.className = "hms-toast" + (type === "success" ? " hms-toast--success" : type === "error" ? " hms-toast--error" : "");
    div.textContent = message;
    host.appendChild(div);
    if (ms > 0) setTimeout(function () { div.remove(); }, ms);
  }

  function openModal(opts) {
    ensureHosts();
    var overlay = document.getElementById("hms-modal-overlay");
    document.getElementById("hms-modal-title").textContent = opts.title || "";
    document.getElementById("hms-modal-body").innerHTML = opts.bodyHtml || "";
    var foot = document.getElementById("hms-modal-footer");
    foot.innerHTML = "";
    (opts.actions || []).forEach(function (a) {
      var btn = document.createElement("button");
      btn.type = "button";
      btn.className = "hms-btn " + (a.primary ? "hms-btn--primary" : "hms-btn--ghost");
      btn.textContent = a.label;
      btn.addEventListener("click", function () {
        if (a.onClick) a.onClick();
        if (a.close !== false) closeModal();
      });
      foot.appendChild(btn);
    });
    overlay.classList.add("is-open");
  }

  function closeModal() {
    var overlay = document.getElementById("hms-modal-overlay");
    if (overlay) overlay.classList.remove("is-open");
  }

  function renderSidebar(activeId) {
    var sidebar = document.getElementById("hms-sidebar");
    if (!sidebar) return;
    var session = getSession();
    var name = session && session.displayName ? session.displayName : "User";
    var role = session && session.role ? session.role : "Authenticated";
    var initials = name.replace(/[^A-Za-z]/g, "").slice(0, 2).toUpperCase() || "HM";
    var allowed = ROLE_PAGES[normalizeRole(role)] || [];
    var links = NAV.filter(function (item) {
      return allowed.indexOf(item.id) >= 0;
    }).map(function (item) {
      var active = item.id === activeId ? " is-active" : "";
      return '<a class="hms-nav__link' + active + '" href="' + item.href + '" data-nav="' + item.id + '">' +
        '<span class="material-symbols-outlined">' + item.icon + "</span>" + item.label + "</a>";
    }).join("");
    sidebar.innerHTML =
      '<div class="hms-sidebar__brand"><h1 class="hms-sidebar__title">MediFlow HMS</h1>' +
      '<p class="hms-sidebar__subtitle">General Hospital</p></div><nav class="hms-nav" aria-label="Main">' +
      links + '</nav><a class="hms-nav__link hms-nav__link--logout" href="index.html" id="hms-logout">' +
      '<span class="material-symbols-outlined">logout</span>Sign out</a><div class="hms-sidebar__user">' +
      '<div class="hms-avatar" aria-hidden="true">' + initials + '</div><div><div class="hms-label" style="color:var(--color-on-surface)">' +
      escapeHtml(name) + '</div><div class="hms-muted">' + escapeHtml(role) + "</div></div></div>";
  }

  function wireMobileNav() {
    var toggle = document.getElementById("hms-menu-toggle");
    var sidebar = document.getElementById("hms-sidebar");
    var backdrop = document.getElementById("hms-sidebar-backdrop");
    if (!toggle || !sidebar) return;
    function close() {
      sidebar.classList.remove("is-open");
      if (backdrop) backdrop.classList.remove("is-visible");
    }
    toggle.addEventListener("click", function () {
      sidebar.classList.toggle("is-open");
      if (backdrop) backdrop.classList.toggle("is-visible");
    });
    if (backdrop) backdrop.addEventListener("click", close);
    sidebar.addEventListener("click", function (e) {
      if (e.target.closest("a.hms-nav__link")) close();
    });
  }

  function wireNotifications() {
    var btn = document.getElementById("hms-notify-btn");
    var panel = document.getElementById("hms-notify-panel");
    if (!btn || !panel) return;
    btn.addEventListener("click", function (e) {
      e.stopPropagation();
      panel.classList.toggle("is-open");
    });
    document.addEventListener("click", function () { panel.classList.remove("is-open"); });
    panel.addEventListener("click", function (e) { e.stopPropagation(); });
    api("/api/notifications").then(function (items) {
      var rows = (items || []).slice(0, 6).map(function (item) {
        return '<div class="hms-notify-item"><strong>' + escapeHtml(item.title) + '</strong><br>' + escapeHtml(item.message) + "</div>";
      }).join("");
      panel.innerHTML = "<h3>Notifications</h3>" + (rows || '<div class="hms-notify-item">No notifications.</div>');
    }).catch(function () {
      panel.innerHTML = '<h3>Notifications</h3><div class="hms-notify-item">Notifications unavailable.</div>';
    });
  }

  function setPageMeta(page) {
    if (DOC_TITLES[page]) document.title = DOC_TITLES[page];
    var h = document.getElementById("hms-page-title");
    if (h && HEADINGS[page]) h.textContent = HEADINGS[page];
    if (page === "dashboard") {
      var session = getSession();
      var title = ROLE_DASHBOARD_TITLE[normalizeRole(session && session.role)];
      if (title) {
        document.title = title + " - MediFlow HMS";
        if (h) h.textContent = title;
      }
    }
  }

  function initAppShell() {
    var page = document.body.getAttribute("data-page");
    if (!page) return;
    if (!requireAuth()) return;
    if (!hasPageAccess(page)) {
      renderSidebar("dashboard");
      setPageMeta(page);
      showAccessDenied("Access Denied");
      return;
    }
    renderSidebar(page);
    setPageMeta(page);
    wireMobileNav();
    wireNotifications();
    var lo = document.getElementById("hms-logout");
    if (lo) lo.addEventListener("click", function (e) {
      e.preventDefault();
      localStorage.removeItem(KEYS.session);
      localStorage.removeItem(KEYS.selectedPatientId);
      localStorage.removeItem(KEYS.billingContext);
      window.location.href = "index.html";
    });
  }

  global.HMS = {
    KEYS: KEYS,
    readJson: readJson,
    writeJson: writeJson,
    getSession: getSession,
    requireAuth: requireAuth,
    showLoading: showLoading,
    hideLoading: hideLoading,
    showToast: showToast,
    openModal: openModal,
    closeModal: closeModal,
    api: api,
    escapeHtml: escapeHtml,
    toPatientUi: toPatientUi,
    loadPatients: loadPatients,
    initAppShell: initAppShell,
    hasPageAccess: hasPageAccess,
    showAccessDenied: showAccessDenied,
    dashboardForRole: dashboardForRole,
    getSelectedPatient: getSelectedPatient,
    getSelectedPatientId: getSelectedPatientId,
    setSelectedPatientId: setSelectedPatientId,
    setSelectedPatient: function (p) { setSelectedPatientId(p && p.dbId); },
    clearSelectedPatient: function () { setSelectedPatientId(null); },
    getBillingContext: function () { return readJson(KEYS.billingContext, null); },
    setBillingContext: function (obj) { writeJson(KEYS.billingContext, obj); },
    navigate: function (href) { window.location.href = href; },
  };

  document.addEventListener("DOMContentLoaded", function () {
    ensureHosts();
    initAppShell();
  });
})(typeof window !== "undefined" ? window : this);
