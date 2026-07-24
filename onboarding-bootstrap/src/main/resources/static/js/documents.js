Session.requireAuth();
renderNavbar('requests');

const urlParams = new URLSearchParams(window.location.search);
const onboardingRequestId = urlParams.get('onboardingRequestId');

if (!onboardingRequestId) {
    document.body.innerHTML = '<div class="page"><div class="alert alert-error">Missing onboardingRequestId in the URL.</div></div>';
    throw new Error('Missing onboardingRequestId');
}

async function loadDocuments() {
    clearAlert('alert');
    try {
        const documents = await apiFetch(`/documents?onboardingRequestId=${encodeURIComponent(onboardingRequestId)}`);
        renderRows(documents || []);
    } catch (err) {
        showAlert('alert', err.message);
    }
}

function renderRows(documents) {
    const body = document.getElementById('documentsBody');
    document.getElementById('emptyState').style.display = documents.length ? 'none' : 'block';
    body.innerHTML = documents.map(d => `
        <tr>
            <td>${escapeHtml(d.fileName)}</td>
            <td>${escapeHtml(d.documentType)}</td>
            <td>${formatBytes(d.fileSizeBytes)}</td>
            <td>${escapeHtml(d.uploadedAt)}</td>
            <td class="actions-cell">
                <button class="small secondary" data-download="${d.id}" data-filename="${escapeHtml(d.fileName)}">Download</button>
            </td>
        </tr>
    `).join('');

    body.querySelectorAll('[data-download]').forEach(btn =>
        btn.addEventListener('click', () => downloadDocument(btn.getAttribute('data-download'), btn.getAttribute('data-filename'))));
}

function formatBytes(bytes) {
    if (!bytes && bytes !== 0) return '';
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

async function downloadDocument(documentId, fileName) {
    try {
        const response = await fetch(`/api/v1/documents/${documentId}/download`, {
            headers: { Authorization: `Bearer ${Session.getToken()}` }
        });
        if (!response.ok) {
            throw new Error(`Download failed (HTTP ${response.status})`);
        }
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = fileName || 'document';
        document.body.appendChild(anchor);
        anchor.click();
        anchor.remove();
        URL.revokeObjectURL(url);
    } catch (err) {
        showAlert('alert', err.message);
    }
}

document.getElementById('uploadForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAlert('alert');
    const uploadBtn = document.getElementById('uploadBtn');
    const fileInput = document.getElementById('fileInput');
    if (!fileInput.files.length) return;

    const employeeId = document.getElementById('employeeId').value.trim();
    if (!isValidUuid(employeeId)) {
        showAlert('alert', 'Employee ID must be a valid UUID, not a name or number.');
        return;
    }

    uploadBtn.disabled = true;
    try {
        const formData = new FormData();
        formData.append('file', fileInput.files[0]);

        const params = new URLSearchParams();
        params.set('onboardingRequestId', onboardingRequestId);
        params.set('employeeId', document.getElementById('employeeId').value.trim());
        params.set('documentType', document.getElementById('documentType').value);

        const response = await fetch(`/api/v1/documents?${params.toString()}`, {
            method: 'POST',
            headers: { Authorization: `Bearer ${Session.getToken()}` },
            body: formData
        });
        if (response.status === 401) {
            Session.logout();
            return;
        }
        if (!response.ok) {
            const body = await response.json().catch(() => ({}));
            throw new Error(body.message || `Upload failed (HTTP ${response.status})`);
        }
        document.getElementById('uploadForm').reset();
        loadDocuments();
    } catch (err) {
        showAlert('alert', err.message);
    } finally {
        uploadBtn.disabled = false;
    }
});

loadDocuments();
