import { showToast } from './toast.js';

const API_BASE = '/api/v1';

export function getToken() {
  return localStorage.getItem('budgetbuddy_token');
}

export function setSession(authData) {
  localStorage.setItem('budgetbuddy_token', authData.token);
  localStorage.setItem('budgetbuddy_user', JSON.stringify(authData.user));
}

export function getUser() {
  return JSON.parse(localStorage.getItem('budgetbuddy_user') || 'null');
}

export function clearSession() {
  localStorage.removeItem('budgetbuddy_token');
  localStorage.removeItem('budgetbuddy_user');
}

export async function api(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  const payload = await response.json().catch(() => ({}));
  if (!response.ok || payload.success === false) {
    const message = payload.message || 'Request failed';
    showToast(message, 'error');
    throw new Error(message);
  }
  return payload.data;
}

export function formatPeso(value) {
  return new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP' }).format(Number(value || 0));
}
