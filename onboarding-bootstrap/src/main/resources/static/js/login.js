document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAlert('alert');

    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Signing in…';

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    try {
        const response = await apiFetch('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        Session.set(response.accessToken, response.username, response.roles || []);
        window.location.href = '/dashboard.html';
    } catch (err) {
        showAlert('alert', err.message || 'Invalid username or password');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Sign in';
    }
});

// Already logged in? skip straight to the dashboard.
if (Session.getToken()) {
    window.location.replace('/dashboard.html');
}
