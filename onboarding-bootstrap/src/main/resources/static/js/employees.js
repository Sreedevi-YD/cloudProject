Session.requireAuth();
renderNavbar('employees');

const state = { page: 0, size: 10, totalPages: 0, editingId: null };

async function loadEmployees() {
    clearAlert('alert');
    const query = document.getElementById('searchInput').value.trim();
    const params = new URLSearchParams();
    if (query) params.set('query', query);
    params.set('page', state.page);
    params.set('size', state.size);

    try {
        const result = await apiFetch(`/employees?${params.toString()}`);
        renderRows(result.content || []);
        state.totalPages = result.totalPages || 0;
        document.getElementById('pageInfo').textContent =
            `Page ${state.totalPages === 0 ? 0 : state.page + 1} of ${state.totalPages} (${result.totalElements} total)`;
        document.getElementById('prevPageBtn').disabled = state.page <= 0;
        document.getElementById('nextPageBtn').disabled = state.page + 1 >= state.totalPages;
    } catch (err) {
        showAlert('alert', err.message);
    }
}

function renderRows(employees) {
    const body = document.getElementById('employeesBody');
    document.getElementById('emptyState').style.display = employees.length ? 'none' : 'block';
    body.innerHTML = employees.map(e => `
        <tr>
            <td>
                <div>${escapeHtml(e.firstName)} ${escapeHtml(e.lastName)}</div>
                <div class="text-muted">${escapeHtml(e.workEmail)}</div>
            </td>
            <td>${escapeHtml(e.employeeCode)}</td>
            <td>${escapeHtml(e.designation)}</td>
            <td>${escapeHtml(e.department)}</td>
            <td>${escapeHtml(e.dateOfJoining)}</td>
            <td>${e.active ? '<span class="badge badge-completed">Active</span>' : '<span class="badge badge-rejected">Inactive</span>'}</td>
            <td class="actions-cell">
                <button class="small secondary" data-edit="${e.id}">Edit</button>
                <button class="small secondary" data-copy="${e.id}" title="Copy employee ID">Copy ID</button>
            </td>
        </tr>
    `).join('');

    body.querySelectorAll('[data-edit]').forEach(btn =>
        btn.addEventListener('click', () => openEditModal(employees.find(e => e.id === btn.getAttribute('data-edit')))));
    body.querySelectorAll('[data-copy]').forEach(btn =>
        btn.addEventListener('click', () => {
            navigator.clipboard.writeText(btn.getAttribute('data-copy'));
            const original = btn.textContent;
            btn.textContent = 'Copied!';
            setTimeout(() => { btn.textContent = original; }, 1200);
        }));
}

function openCreateModal() {
    state.editingId = null;
    document.getElementById('employeeModalTitle').textContent = 'New Employee';
    document.getElementById('employeeForm').reset();
    document.getElementById('workEmail').disabled = false;
    document.getElementById('dateOfJoiningWrap').style.display = 'block';
    document.getElementById('activeWrap').style.display = 'none';
    document.getElementById('dateOfJoining').required = true;
    clearAlert('employeeAlert');
    document.getElementById('employeeModal').style.display = 'flex';
}

function openEditModal(employee) {
    if (!employee) return;
    state.editingId = employee.id;
    document.getElementById('employeeModalTitle').textContent = `Edit ${employee.firstName} ${employee.lastName}`;
    document.getElementById('firstName').value = employee.firstName;
    document.getElementById('lastName').value = employee.lastName;
    document.getElementById('workEmail').value = employee.workEmail;
    document.getElementById('workEmail').disabled = true;
    document.getElementById('personalEmail').value = employee.personalEmail || '';
    document.getElementById('phoneNumber').value = employee.phoneNumber || '';
    document.getElementById('managerId').value = employee.managerId || '';
    document.getElementById('designation').value = employee.designation || '';
    document.getElementById('department').value = employee.department || '';
    document.getElementById('dateOfJoiningWrap').style.display = 'none';
    document.getElementById('dateOfJoining').required = false;
    document.getElementById('activeWrap').style.display = 'block';
    document.getElementById('activeSelect').value = String(employee.active);
    clearAlert('employeeAlert');
    document.getElementById('employeeModal').style.display = 'flex';
}

document.getElementById('newEmployeeBtn').addEventListener('click', openCreateModal);
document.getElementById('cancelEmployeeBtn').addEventListener('click', () => {
    document.getElementById('employeeModal').style.display = 'none';
});

document.getElementById('employeeForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAlert('employeeAlert');

    const managerId = document.getElementById('managerId').value.trim();
    if (managerId && !isValidUuid(managerId)) {
        showAlert('employeeAlert', 'Manager user ID must be a valid UUID (e.g. copied from another user\'s record), not a name or number.');
        return;
    }

    const submitBtn = document.getElementById('employeeSubmitBtn');
    submitBtn.disabled = true;

    try {
        if (state.editingId) {
            await apiFetch(`/employees/${state.editingId}`, {
                method: 'PUT',
                body: JSON.stringify({
                    firstName: document.getElementById('firstName').value.trim(),
                    lastName: document.getElementById('lastName').value.trim(),
                    phoneNumber: document.getElementById('phoneNumber').value.trim(),
                    designation: document.getElementById('designation').value.trim(),
                    department: document.getElementById('department').value.trim(),
                    managerId: document.getElementById('managerId').value.trim() || null,
                    personalEmail: document.getElementById('personalEmail').value.trim() || null,
                    active: document.getElementById('activeSelect').value === 'true'
                })
            });
        } else {
            await apiFetch('/employees', {
                method: 'POST',
                body: JSON.stringify({
                    firstName: document.getElementById('firstName').value.trim(),
                    lastName: document.getElementById('lastName').value.trim(),
                    personalEmail: document.getElementById('personalEmail').value.trim() || null,
                    workEmail: document.getElementById('workEmail').value.trim(),
                    phoneNumber: document.getElementById('phoneNumber').value.trim(),
                    designation: document.getElementById('designation').value.trim(),
                    department: document.getElementById('department').value.trim(),
                    managerId: document.getElementById('managerId').value.trim() || null,
                    dateOfJoining: document.getElementById('dateOfJoining').value
                })
            });
        }
        document.getElementById('employeeModal').style.display = 'none';
        loadEmployees();
    } catch (err) {
        showAlert('employeeAlert', err.message);
    } finally {
        submitBtn.disabled = false;
    }
});

document.getElementById('searchBtn').addEventListener('click', () => { state.page = 0; loadEmployees(); });
document.getElementById('searchInput').addEventListener('keydown', (e) => {
    if (e.key === 'Enter') { state.page = 0; loadEmployees(); }
});
document.getElementById('prevPageBtn').addEventListener('click', () => { state.page--; loadEmployees(); });
document.getElementById('nextPageBtn').addEventListener('click', () => { state.page++; loadEmployees(); });

loadEmployees();
