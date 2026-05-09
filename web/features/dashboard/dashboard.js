import { api, clearSession, formatPeso } from '../../shared/js/api.js';
import { showToast } from '../../shared/js/toast.js';

async function loadDashboard() {
  const [summary, transactions] = await Promise.all([
    api('/transactions/summary'),
    api('/transactions')
  ]);
  document.getElementById('balance').textContent = formatPeso(summary.balance);
  document.getElementById('income').textContent = `↑ ${formatPeso(summary.totalIncome)}`;
  document.getElementById('expense').textContent = `↓ ${formatPeso(summary.totalExpense)}`;
  document.getElementById('count').textContent = summary.count;
  document.getElementById('transactions').innerHTML = transactions.length ? transactions.map(transaction => `
    <div class="list-row">
      <div><strong>${transaction.category}</strong><br><span>${transaction.type}</span></div>
      <span class="amount-pill ${transaction.type === 'INCOME' ? 'income' : 'expense'} amount">
        ${transaction.type === 'INCOME' ? '↑' : '↓'} ${formatPeso(transaction.amount)}
      </span>
    </div>
  `).join('') : '<div class="empty-state">No transactions yet — add your first one!</div>';
}

document.getElementById('transactionForm').addEventListener('submit', async event => {
  event.preventDefault();
  await api('/transactions', {
    method: 'POST',
    body: JSON.stringify({
      type: document.getElementById('type').value,
      amount: document.getElementById('amount').value,
      category: document.getElementById('category').value
    })
  });
  event.target.reset();
  showToast('Transaction saved');
  loadDashboard();
});

document.getElementById('logoutBtn').addEventListener('click', () => {
  clearSession();
  window.location.href = '../auth/login.html';
});

loadDashboard();
