Session.requireAuth();
renderNavbar('audit');

const state = { page: 0, size: 20, totalPages: 0 };

function renderRows(logs) {
    const body = document.getElementById('logsBody');
    document.getElementById('emptyState').style.display = logs.length ? 'none' : 'block';
    body.innerHTML = logs.map(l => `
        <tr>
            <td>${escapeHtml(l.entityType)}</td>
            <td class="text-muted">${escapeHtml(l.entityId)}</td>
            <td>${badge(l.action)}</td>
            <td class="text-muted">${escapeHtml(l.performedByUserId)}</td>
            <td>${escapeHtml(l.occurredAt)}</td>
            <td class="text-muted">${escapeHtml(l.details)}</td>
        </tr>
    `).join('');
}

async function loadAll() {
    clearAlert('alert');
    document.getElementById('paginationControls').style.display = 'flex';
    try {
        const result = await apiFetch(`/audit-logs?page=${state.page}&size=${state.size}`);
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

async function loadForEntity(entityType, entityId) {
    clearAlert('alert');
    document.getElementById('paginationControls').style.display = 'none';
    try {
        const logs = await apiFetch(`/audit-logs/${encodeURIComponent(entityType)}/${encodeURIComponent(entityId)}`);
        renderRows(logs || []);
    } catch (err) {
        showAlert('alert', err.message);
    }
}

document.getElementById('lookupBtn').addEventListener('click', () => {
    const entityType = document.getElementById('entityTypeFilter').value;
    const entityId = document.getElementById('entityIdFilter').value.trim();
    if (!entityType || !entityId) {
        showAlert('alert', 'Pick an entity type and enter an entity ID to look up.');
        return;
    }
    loadForEntity(entityType, entityId);
});

document.getElementById('clearLookupBtn').addEventListener('click', () => {
    document.getElementById('entityTypeFilter').value = '';
    document.getElementById('entityIdFilter').value = '';
    state.page = 0;
    loadAll();
});

document.getElementById('prevPageBtn').addEventListener('click', () => { state.page--; loadAll(); });
document.getElementById('nextPageBtn').addEventListener('click', () => { state.page++; loadAll(); });

loadAll();
