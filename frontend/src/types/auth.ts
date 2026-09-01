export type Role = 'FARMER' | 'BUYER' | 'ADMIN';

export type BuyerType = 'WHOLESALER' | 'RETAILER' | 'PROCESSOR' | 'EXPORTER' | 'INDIVIDUAL';

export type AccountStatus = 'PENDING' | 'ACTIVE' | 'SUSPENDED' | 'REJECTED';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface FarmerProfileRequest {
  farmSizeAcres?: number;
  village?: string;
  district: string;
  state: string;
  pincode?: string;
  primaryCrops?: string;
}

export interface BuyerProfileRequest {
  businessName: string;
  buyerType: BuyerType;
  gstin?: string;
  address?: string;
  district: string;
  state: string;
  pincode?: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phoneNumber: string;
  role: Role;
  farmerProfile?: FarmerProfileRequest;
  buyerProfile?: BuyerProfileRequest;
}

export interface FarmerProfileResponse {
  id: number;
  farmSizeAcres?: number;
  village?: string;
  district: string;
  state: string;
  pincode?: string;
  primaryCrops?: string;
  isVerified?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface BuyerProfileResponse {
  id: number;
  businessName: string;
  buyerType: BuyerType;
  gstin?: string;
  address?: string;
  district: string;
  state: string;
  pincode?: string;
  isVerified?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface UserResponse {
  id: number;
  name: string;
  email: string;
  phoneNumber: string;
  role: Role;
  status: AccountStatus;
  farmerProfile?: FarmerProfileResponse;
  buyerProfile?: BuyerProfileResponse;
  createdAt?: string;
  updatedAt?: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserResponse;
}

export interface ErrorResponse {
  timestamp?: string;
  status: number;
  error: string;
  message: string;
  path?: string;
  validationErrors?: Record<string, string>;
}
