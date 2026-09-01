import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';

const TOKEN_KEY = 'agrilink_auth_token';

// Create a centralized Axios instance
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach Bearer token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle global auth errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Clear token and auth state on 401 Unauthorized
      useAuthStore.getState().logout();
    }
    return Promise.reject(error);
  }
);
