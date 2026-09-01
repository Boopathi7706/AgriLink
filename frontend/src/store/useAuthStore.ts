import { create } from 'zustand';

// Placeholder store for future authentication
// DO NOT implement JWT logic here during Phase 0

interface AuthState {
  isAuthenticated: boolean;
  // user: User | null;
  // token: string | null;
  // login: () => void;
  // logout: () => void;
}

export const useAuthStore = create<AuthState>((/*set*/) => ({
  isAuthenticated: false,
  // Future state logic goes here
}));
