import axios from 'axios';

// Create a centralized Axios instance
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor (prepared for future auth tokens)
apiClient.interceptors.request.use(
  (config) => {
    // In Phase 1, we will attach the Authorization Bearer token here
    // const token = useAuthStore.getState().token;
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor (prepared for global error handling)
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Basic error normalization structure
    if (error.response?.status === 401) {
      // Future: handle unauthorized (e.g. redirect to login)
    }
    return Promise.reject(error);
  }
);
