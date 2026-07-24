Session.requireAuth();
renderNavbar('dashboard');

document.getElementById('welcomeName').textContent = `, ${Session.getUsername()}`;

const allCards = [
    { href: '/employees.html', title: 'Employees', desc: 'Create and update employee profiles, search the directory.', roles: ['ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER'] },
    { href: '/onboarding-requests.html', title: 'Onboarding Requests', desc: 'Create requests, approve or reject them, and track status through completion.', roles: ['ROLE_ADMIN', 'ROLE_HR', 'ROLE_MANAGER'] },
    { href: '/tasks.html', title: 'Task Dashboard', desc: 'Department task queues auto-created on approval — asset provisioning, account setup, orientation.', roles: null },
    { href: '/audit-logs.html', title: 'Audit Logs', desc: 'Every create/update/approve/reject/upload action, with who and when.', roles: ['ROLE_ADMIN'] },
    { href: '/users.html', title: 'Users', desc: 'Register new login accounts and assign roles.', roles: ['ROLE_ADMIN'] }
];

document.getElementById('navCards').innerHTML = allCards
    .filter(c => !c.roles || Session.hasAnyRole(...c.roles))
    .map(c => `
        <a class="nav-card" href="${c.href}">
            <div class="nav-card-title">${c.title}</div>
            <div class="nav-card-desc">${c.desc}</div>
        </a>
    `).join('');

const roleDescriptions = {
    ROLE_ADMIN: 'Full access to every module, including audit logs.',
    ROLE_HR: 'Create onboarding requests, manage employee profiles, review documents.',
    ROLE_MANAGER: 'Approve or reject onboarding requests for your reports.',
    ROLE_IT: 'Allocate assets and accounts, work IT tasks.',
    ROLE_EMPLOYEE: 'Upload documents and complete your own onboarding tasks.'
};

const roles = Session.getRoles();
document.getElementById('roleSummary').innerHTML = roles
    .map(r => `<strong>${r.replace('ROLE_', '')}</strong> — ${roleDescriptions[r] || ''}`)
    .join('<br>');
