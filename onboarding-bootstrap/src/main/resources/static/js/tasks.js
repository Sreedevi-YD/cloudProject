Session.requireAuth();
renderNavbar('tasks');

const urlParams = new URLSearchParams(window.location.search);
const onboardingRequestId = urlParams.get('onboardingRequestId');

if (onboardingRequestId) {
    document.getElementById('pageTitle').textContent = 'Tasks for this Onboarding Request';
    document.getElementById('filterToolbar').style.display = 'none';
}

async function loadTasks() {
    clearAlert('alert');
    try {
        let tasks;
        if (onboardingRequestId) {
            tasks = await apiFetch(`/tasks?onboardingRequestId=${encodeURIComponent(onboardingRequestId)}`);
        } else {
            const department = document.getElementById('departmentFilter').value;
            const params = new URLSearchParams();
            if (department) params.set('department', department);
            tasks = await apiFetch(`/tasks/dashboard?${params.toString()}`);
        }
        renderRows(tasks || []);
    } catch (err) {
        showAlert('alert', err.message);
    }
}

function renderRows(tasks) {
    const body = document.getElementById('tasksBody');
    document.getElementById('emptyState').style.display = tasks.length ? 'none' : 'block';
    body.innerHTML = tasks.map(t => `
        <tr>
            <td>${escapeHtml(t.title)}</td>
            <td class="text-muted">${escapeHtml(t.description)}</td>
            <td>${escapeHtml(t.owningDepartment)}</td>
            <td>${badge(t.status)}</td>
            <td class="actions-cell">
                ${t.status === 'PENDING' ? `<button class="small secondary" data-inprogress="${t.id}">Start</button>` : ''}
                ${t.status !== 'COMPLETED' ? `<button class="small" data-complete="${t.id}">Mark complete</button>` : ''}
            </td>
        </tr>
    `).join('');

    body.querySelectorAll('[data-inprogress]').forEach(btn =>
        btn.addEventListener('click', () => updateStatus(btn.getAttribute('data-inprogress'), 'IN_PROGRESS')));
    body.querySelectorAll('[data-complete]').forEach(btn =>
        btn.addEventListener('click', () => updateStatus(btn.getAttribute('data-complete'), 'COMPLETED')));
}

async function updateStatus(taskId, status) {
    try {
        await apiFetch(`/tasks/${taskId}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status })
        });
        loadTasks();
    } catch (err) {
        showAlert('alert', err.message);
    }
}

document.getElementById('filterBtn').addEventListener('click', loadTasks);

loadTasks();
