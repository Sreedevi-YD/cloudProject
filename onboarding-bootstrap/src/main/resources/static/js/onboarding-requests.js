Session.requireAuth();
renderNavbar('requests');

const state = { page: 0, size: 10, totalPages: 0, rejectingId: null };

async function loadRequests() {
    clearAlert('alert');
    const query = document.getElementById('searchInput').value.trim();
    const status = document.getElementById('statusFilter').value;

    const params = new URLSearchParams();
    if (query) params.set('query', query);
    if (status) params.set('status', status);
    params.set('page', state.page);
    params.set('size', state.size);

    try {
        const result = await apiFetch(`/onboarding-requests?${params.toString()}`);
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

function renderRows(requests) {
    const body = document.getElementById('requestsBody');
    document.getElementById('emptyState').style.display = requests.length ? 'none' : 'block';
    body.innerHTML = requests.map(r => `
        <tr>
            <td>
                <div>${escapeHtml(r.candidateName)}</div>
                <div class="text-muted">${escapeHtml(r.candidateEmail)}</div>
            </td>
            <td>${escapeHtml(r.designation)}</td>
            <td>${escapeHtml(r.department)}</td>
            <td>${escapeHtml(r.proposedJoiningDate)}</td>
            <td>${badge(r.status)}</td>
            <td class="actions-cell">
                ${r.status === 'PENDING_APPROVAL' ? `
                    <button class="small" data-approve="${r.id}">Approve</button>
                    <button class="small danger" data-reject="${r.id}">Reject</button>
                ` : ''}
                <a href="/tasks.html?onboardingRequestId=${r.id}"><button class="small secondary" type="button">Tasks</button></a>
                <a href="/documents.html?onboardingRequestId=${r.id}"><button class="small secondary" type="button">Documents</button></a>
                <a href="/assets.html?onboardingRequestId=${r.id}"><button class="small secondary" type="button">Assets</button></a>
            </td>
        </tr>
    `).join('');

    body.querySelectorAll('[data-approve]').forEach(btn =>
        btn.addEventListener('click', () => approveRequest(btn.getAttribute('data-approve'))));
    body.querySelectorAll('[data-reject]').forEach(btn =>
        btn.addEventListener('click', () => openRejectModal(btn.getAttribute('data-reject'))));
}

async function approveRequest(id) {
    try {
        await apiFetch(`/onboarding-requests/${id}/approve`, { method: 'POST' });
        loadRequests();
    } catch (err) {
        showAlert('alert', err.message);
    }
}

function openRejectModal(id) {
    state.rejectingId = id;
    document.getElementById('rejectReason').value = '';
    document.getElementById('rejectModal').style.display = 'flex';
}

document.getElementById('cancelRejectBtn').addEventListener('click', () => {
    document.getElementById('rejectModal').style.display = 'none';
});

document.getElementById('rejectForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await apiFetch(`/onboarding-requests/${state.rejectingId}/reject`, {
            method: 'POST',
            body: JSON.stringify({ reason: document.getElementById('rejectReason').value })
        });
        document.getElementById('rejectModal').style.display = 'none';
        loadRequests();
    } catch (err) {
        showAlert('alert', err.message);
    }
});

document.getElementById('searchBtn').addEventListener('click', () => { state.page = 0; loadRequests(); });
document.getElementById('searchInput').addEventListener('keydown', (e) => {
    if (e.key === 'Enter') { state.page = 0; loadRequests(); }
});
document.getElementById('statusFilter').addEventListener('change', () => { state.page = 0; loadRequests(); });
document.getElementById('prevPageBtn').addEventListener('click', () => { state.page--; loadRequests(); });
document.getElementById('nextPageBtn').addEventListener('click', () => { state.page++; loadRequests(); });

document.getElementById('newRequestBtn').addEventListener('click', () => {
    document.getElementById('createForm').reset();
    clearAlert('createAlert');
    document.getElementById('createModal').style.display = 'flex';
});
document.getElementById('cancelCreateBtn').addEventListener('click', () => {
    document.getElementById('createModal').style.display = 'none';
});

document.getElementById('createForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAlert('createAlert');

    const hiringManagerId = document.getElementById('hiringManagerId').value.trim();
    if (!isValidUuid(hiringManagerId)) {
        showAlert('createAlert', 'Hiring manager user ID must be a valid UUID, not a name or number.');
        return;
    }

    const submitBtn = document.getElementById('createSubmitBtn');
    submitBtn.disabled = true;
    try {
        await apiFetch('/onboarding-requests', {
            method: 'POST',
            body: JSON.stringify({
                candidateName: document.getElementById('candidateName').value.trim(),
                candidateEmail: document.getElementById('candidateEmail').value.trim(),
                designation: document.getElementById('designation').value.trim(),
                department: document.getElementById('department').value.trim(),
                proposedJoiningDate: document.getElementById('proposedJoiningDate').value,
                hiringManagerId: document.getElementById('hiringManagerId').value.trim()
            })
        });
        document.getElementById('createModal').style.display = 'none';
        state.page = 0;
        loadRequests();
    } catch (err) {
        showAlert('createAlert', err.message);
    } finally {
        submitBtn.disabled = false;
    }
});

loadRequests();
