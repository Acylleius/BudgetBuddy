import { api } from '../../shared/js/api.js';
import { showToast } from '../../shared/js/toast.js';

async function loadProfile() {
  const user = await api('/users/me');
  document.getElementById('profile').innerHTML = `
    <p><strong>${user.firstname} ${user.lastname}</strong></p>
    <p>${user.email}</p>
    <span class="badge ${user.authProvider === 'google' ? 'badge-member' : 'badge-admin'}">${user.authProvider}</span>
  `;
  if (user.authProvider === 'google') {
    document.getElementById('passwordCard').querySelector('.card-body').innerHTML =
      '<p>You signed in with Google. Password management is handled by your Google account.</p>';
  }
}

document.getElementById('passwordForm').addEventListener('submit', async event => {
  event.preventDefault();
  await api('/auth/change-password', {
    method: 'POST',
    body: JSON.stringify({
      currentPassword: document.getElementById('currentPassword').value,
      newPassword: document.getElementById('newPassword').value
    })
  });
  event.target.reset();
  showToast('Password updated');
});

loadProfile();
