import { apiClient } from './client';
import type {
  UpdateFarmerProfileRequest,
  UpdateBuyerProfileRequest,
  FarmerProfileResponse,
  BuyerProfileResponse,
} from '@/types/profile';

export const updateFarmerProfileApi = async (
  data: UpdateFarmerProfileRequest
): Promise<FarmerProfileResponse> => {
  const response = await apiClient.put<FarmerProfileResponse>('/api/v1/users/me/farmer-profile', data);
  return response.data;
};

export const updateBuyerProfileApi = async (
  data: UpdateBuyerProfileRequest
): Promise<BuyerProfileResponse> => {
  const response = await apiClient.put<BuyerProfileResponse>('/api/v1/users/me/buyer-profile', data);
  return response.data;
};
