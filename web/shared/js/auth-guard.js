import { getToken } from './api.js';

if (!getToken()) {
  window.location.href = '../auth/login.html';
}
