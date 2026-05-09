import { api, formatPeso } from '../../shared/js/api.js';
import { showToast } from '../../shared/js/toast.js';

const groupId = new URLSearchParams(window.location.search).get('id');

async function loadGroup() {
  const [group, expenses, balances] = await Promise.all([
    api(`/groups/${groupId}`),
    api(`/groups/${groupId}/shared-expenses`),
    api(`/groups/${groupId}/balances`)
  ]);
  document.getElementById('groupTitle').textContent = group.name;
  document.getElementById('members').innerHTML = group.members.map(member => `
    <div class="list-row">
      <span>${member.firstname} ${member.lastname}<br>${member.email}</span>
      <span class="badge ${member.role === 'ADMIN' ? 'badge-admin' : 'badge-member'}">${member.role}</span>
    </div>
  `).join('');
  document.getElementById('expenses').innerHTML = expenses.length ? expenses.map(expense => `
    <div class="list-row">
      <div><strong>${expense.category}</strong><br><span>Paid by #${expense.paidBy}</span></div>
      <span class="amount amount-pill expense">↓ ${formatPeso(expense.amount)}</span>
    </div>
  `).join('') : '<div class="empty-state">No expenses yet — add your first one!</div>';
  document.getElementById('balances').innerHTML = balances.length ? balances.map(balance => `
    <div class="list-row">
      <span>User #${balance.userId}</span>
      <span class="amount amount-pill ${Number(balance.netBalance) >= 0 ? 'income' : 'expense'}">${formatPeso(balance.netBalance)}</span>
    </div>
  `).join('') : '<div class="empty-state">No balances yet.</div>';
}

document.getElementById('memberForm').addEventListener('submit', async event => {
  event.preventDefault();
  await api(`/groups/${groupId}/members`, {
    method: 'POST',
    body: JSON.stringify({ email: document.getElementById('memberEmail').value })
  });
  event.target.reset();
  showToast('Member added');
  loadGroup();
});

document.getElementById('expenseForm').addEventListener('submit', async event => {
  event.preventDefault();
  await api(`/groups/${groupId}/shared-expenses`, {
    method: 'POST',
    body: JSON.stringify({
      paidBy: Number(document.getElementById('paidBy').value),
      amount: document.getElementById('expenseAmount').value,
      category: document.getElementById('expenseCategory').value
    })
  });
  event.target.reset();
  showToast('Expense saved');
  loadGroup();
});

loadGroup();
