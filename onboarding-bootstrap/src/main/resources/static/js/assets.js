Session.requireAuth();
renderNavbar('requests');

const urlParams = new URLSearchParams(window.location.search);
const onboardingRequestId = urlParams.get('onboardingRequestId');

if (!onboardingRequestId) {
    document.body.innerHTML = '<div class="page"><div class="alert alert-error">Missing onboardingRequestId in the URL.</div></div>';
    throw new Error('Missing onboardingRequestId');
}

async function loadAssets() {
    clearAlert('alert');
    try {
        const assets = await apiFetch(`/assets?onboardingRequestId=${encodeURIComponent(onboardingRequestId)}`);
        renderRows(assets || []);
    } catch (err) {
        showAlert('alert', err.message);
    }
}

function renderRows(assets) {
    const body = document.getElementById('assetsBody');
    document.getElementById('emptyState').style.display = assets.length ? 'none' : 'block';
    body.innerHTML = assets.map(a => `
        <tr>
            <td>${escapeHtml(a.assetType)}</td>
            <td>${escapeHtml(a.assetTag)}</td>
            <td>${badge(a.status)}</td>
            <td>${escapeHtml(a.assignedAt)}</td>
            <td class="actions-cell">
                ${a.status === 'ASSIGNED' ? `<button class="small secondary" data-return="${a.id}">Mark returned</button>` : ''}
            </td>
        </tr>
    `).join('');

    body.querySelectorAll('[data-return]').forEach(btn =>
        btn.addEventListener('click', () => returnAsset(btn.getAttribute('data-return'))));
}

async function returnAsset(assetId) {
    try {
        await apiFetch(`/assets/${assetId}/return`, { method: 'POST' });
        loadAssets();
    } catch (err) {
        showAlert('alert', err.message);
    }
}

document.getElementById('assignForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAlert('alert');

    const employeeId = document.getElementById('employeeId').value.trim();
    if (!isValidUuid(employeeId)) {
        showAlert('alert', 'Employee ID must be a valid UUID, not a name or number.');
        return;
    }

    const assignBtn = document.getElementById('assignBtn');
    assignBtn.disabled = true;
    try {
        const params = new URLSearchParams();
        params.set('onboardingRequestId', onboardingRequestId);
        params.set('employeeId', document.getElementById('employeeId').value.trim());

        await apiFetch(`/assets?${params.toString()}`, {
            method: 'POST',
            body: JSON.stringify({
                assetType: document.getElementById('assetType').value,
                assetTag: document.getElementById('assetTag').value.trim()
            })
        });
        document.getElementById('assignForm').reset();
        loadAssets();
    } catch (err) {
        showAlert('alert', err.message);
    } finally {
        assignBtn.disabled = false;
    }
});

loadAssets();
