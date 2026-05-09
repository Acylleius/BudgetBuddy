import { api, setSession } from '../../shared/js/api.js';
import { showToast } from '../../shared/js/toast.js';

document.getElementById('loginForm').addEventListener('submit', async event => {
  event.preventDefault();
  const data = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({
      email: document.getElementById('loginEmail').value,
      password: document.getElementById('loginPassword').value
    })
  });
  setSession(data);
  showToast('Login successful');
  window.location.href = '../dashboard/dashboard.html';
});

document.getElementById('registerForm').addEventListener('submit', async event => {
  event.preventDefault();
  const data = await api('/auth/register', {
    method: 'POST',
    body: JSON.stringify({
      firstname: document.getElementById('firstname').value,
      lastname: document.getElementById('lastname').value,
      email: document.getElementById('registerEmail').value,
      password: document.getElementById('registerPassword').value
    })
  });
  setSession(data);
  showToast('Account created');
  window.location.href = '../dashboard/dashboard.html';
});
