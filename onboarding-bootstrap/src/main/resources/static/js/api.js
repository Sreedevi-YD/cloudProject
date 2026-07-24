// Shared session + fetch helper for every page. No build step, no framework —
// plain fetch() against the same REST API the backend already exposes.
const API_BASE = '/api/v1';

const Session = {
    getToken() {
        return localStorage.getItem('token');
    },
    getUsername() {
        return localStorage.getItem('username');
    },
    getRoles() {
        try {
            return JSON.parse(localStorage.getItem('roles') || '[]');
        } catch {
            return [];
        }
    },
    hasAnyRole(...roles) {
        const mine = Session.getRoles();
        return roles.some(r => mine.includes(r));
    },
    set(token, username, roles) {
        localStorage.setItem('token', token);
        localStorage.setItem('username', username);
        localStorage.setItem('roles', JSON.stringify(roles));
    },
    clear() {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('roles');
    },
    requireAuth() {
        if (!Session.getToken()) {
            window.location.href = '/login.html';
        }
    },
    logout() {
        Session.clear();
        window.location.href = '/login.html';
    }
};

async function apiFetch(path, options = {}) {
    const token = Session.getToken();
    const isFormData = options.body instanceof FormData;
    const headers = Object.assign({}, options.headers);
    if (!isFormData) {
        headers['Content-Type'] = 'application/json';
    }
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(API_BASE + path, { ...options, headers });

    if (response.status === 401) {
        Session.clear();
        window.location.href = '/login.html';
        throw new Error('Session expired. Please log in again.');
    }

    if (!response.ok) {
        let message = `Request failed (HTTP ${response.status})`;
        try {
            const body = await response.json();
            message = body.message || message;
        } catch {
            // response wasn't JSON; keep the default message
        }
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }
    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
        return response.json();
    }
    return response.text();
}

function renderNavbar(activePage) {
    const el = document.getElementById('navbar');
    if (!el) return;
    const username = Session.getUsername() || '';
    const roles = Session.getRoles();

    const link = (href, label, key) =>
        `<a href="${href}" class="${activePage === key ? 'active' : ''}">${label}</a>`;

    const links = [link('/dashboard.html', 'Dashboard', 'dashboard')];
    if (Session.hasAnyRole('ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER')) {
        links.push(link('/employees.html', 'Employees', 'employees'));
    }
    links.push(link('/onboarding-requests.html', 'Onboarding Requests', 'requests'));
    links.push(link('/tasks.html', 'Tasks', 'tasks'));
    if (Session.hasAnyRole('ROLE_ADMIN')) {
        links.push(link('/audit-logs.html', 'Audit Logs', 'audit'));
        links.push(link('/users.html', 'Users', 'users'));
    }

    el.innerHTML = `
        <div class="brand">Employee Onboarding Portal</div>
        <nav>
            ${links.join('')}
        </nav>
        <div class="user-info">
            <span>${username} <span class="text-muted">(${roles.join(', ')})</span></span>
            <button class="logout" id="logoutBtn">Log out</button>
        </div>
    `;
    document.getElementById('logoutBtn').addEventListener('click', Session.logout);
}

const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

function isValidUuid(value) {
    return UUID_PATTERN.test((value || '').trim());
}

function badge(status) {
    const cls = (status || '').toLowerCase();
    return `<span class="badge badge-${cls}">${(status || '').replace(/_/g, ' ')}</span>`;
}

function escapeHtml(value) {
    if (value === null || value === undefined) return '';
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function showAlert(containerId, message, type = 'error') {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = `<div class="alert alert-${type}">${escapeHtml(message)}</div>`;
}

function clearAlert(containerId) {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = '';
}
