import type { BuyerType, FarmerProfileResponse, BuyerProfileResponse } from './auth';

export type { BuyerType, FarmerProfileResponse, BuyerProfileResponse };

export interface UpdateFarmerProfileRequest {
  farmSizeAcres?: number;
  village?: string;
  district: string;
  state: string;
  pincode?: string;
  primaryCrops?: string;
}

export interface UpdateBuyerProfileRequest {
  businessName: string;
  buyerType: BuyerType;
  gstin?: string;
  address?: string;
  district: string;
  state: string;
  pincode?: string;
}

export const BUYER_TYPE_OPTIONS: { value: BuyerType; label: string }[] = [
  { value: 'WHOLESALER', label: 'Wholesaler' },
  { value: 'RETAILER', label: 'Retailer' },
  { value: 'PROCESSOR', label: 'Processor' },
  { value: 'EXPORTER', label: 'Exporter' },
  { value: 'INDIVIDUAL', label: 'Individual' },
];
