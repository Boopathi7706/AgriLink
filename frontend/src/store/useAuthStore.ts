import { create } from 'zustand';
import type { UserResponse } from '@/types/auth';
import { getCurrentUserApi } from '@/api/users';

const TOKEN_KEY = 'agrilink_auth_token';

interface AuthState {
  token: string | null;
  user: UserResponse | null;
  isAuthenticated: boolean;
  isHydrating: boolean;
  setAuth: (token: string, user: UserResponse) => void;
  setUser: (user: UserResponse) => void;
  logout: () => void;
  hydrate: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  token: localStorage.getItem(TOKEN_KEY),
  user: null,
  isAuthenticated: false,
  isHydrating: true,

  setAuth: (token: string, user: UserResponse) => {
    localStorage.setItem(TOKEN_KEY, token);
    set({
      token,
      user,
      isAuthenticated: true,
      isHydrating: false,
    });
  },

  setUser: (user: UserResponse) => {
    set({ user });
  },

  logout: () => {
    localStorage.removeItem(TOKEN_KEY);
    set({
      token: null,
      user: null,
      isAuthenticated: false,
      isHydrating: false,
    });
  },

  hydrate: async () => {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
      set({ isHydrating: false, isAuthenticated: false, user: null, token: null });
      return;
    }

    try {
      set({ token, isHydrating: true });
      const user = await getCurrentUserApi();
      set({
        user,
        isAuthenticated: true,
        isHydrating: false,
      });
    } catch {
      // If token is invalid or expired, clear authentication
      get().logout();
    }
  },
}));
