import { api } from '../../shared/js/api.js';
import { showToast } from '../../shared/js/toast.js';

async function loadGroups() {
  const groups = await api('/groups');
  document.getElementById('groupsList').innerHTML = groups.length ? groups.map(group => `
    <div class="list-row">
      <div><strong>${group.name}</strong><br><span>${group.description || ''}</span></div>
      <a class="btn-secondary" href="group-detail.html?id=${group.id}">Open</a>
    </div>
  `).join('') : '<div class="empty-state">No groups yet — create your first one!</div>';
}

document.getElementById('groupForm').addEventListener('submit', async event => {
  event.preventDefault();
  await api('/groups', {
    method: 'POST',
    body: JSON.stringify({
      name: document.getElementById('groupName').value,
      description: document.getElementById('groupDescription').value
    })
  });
  event.target.reset();
  showToast('Group created');
  loadGroups();
});

loadGroups();
