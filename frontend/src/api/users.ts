import { apiClient } from './client';
import type { UserResponse } from '@/types/auth';

export const getCurrentUserApi = async (): Promise<UserResponse> => {
  const response = await apiClient.get<UserResponse>('/api/v1/users/me');
  return response.data;
};
