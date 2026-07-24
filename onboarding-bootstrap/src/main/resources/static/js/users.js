Session.requireAuth();
renderNavbar('users');

if (!Session.hasAnyRole('ROLE_ADMIN')) {
    document.body.innerHTML = '<div class="page"><div class="alert alert-error">Admins only.</div></div>';
    throw new Error('Not authorized');
}

document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAlert('alert');

    const roles = Array.from(document.querySelectorAll('.role-checkbox:checked')).map(cb => cb.value);
    if (roles.length === 0) {
        showAlert('alert', 'Select at least one role.');
        return;
    }

    const employeeId = document.getElementById('employeeId').value.trim();
    if (employeeId && !isValidUuid(employeeId)) {
        showAlert('alert', 'Employee ID must be a valid UUID, not a name or number.');
        return;
    }

    const registerBtn = document.getElementById('registerBtn');
    registerBtn.disabled = true;
    try {
        await apiFetch('/auth/register', {
            method: 'POST',
            body: JSON.stringify({
                username: document.getElementById('username').value.trim(),
                email: document.getElementById('email').value.trim(),
                password: document.getElementById('password').value,
                roles,
                employeeId: document.getElementById('employeeId').value.trim() || null
            })
        });
        showAlert('alert', `User created successfully.`, 'success');
        document.getElementById('registerForm').reset();
    } catch (err) {
        showAlert('alert', err.message);
    } finally {
        registerBtn.disabled = false;
    }
});
