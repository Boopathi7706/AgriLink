import { apiClient } from './client';
import type {
  CommodityResponse,
  MandiResponse,
  MarketPriceResponse,
  PriceDiscoveryResponse,
  RecordMarketPriceRequest,
  MarketPriceFilters,
} from '@/types/marketPrice';

export const getCommoditiesApi = async (category?: string): Promise<CommodityResponse[]> => {
  const params: Record<string, string> = {};
  if (category && category.trim() !== '') {
    params.category = category.trim();
  }
  const response = await apiClient.get<CommodityResponse[]>('/api/v1/commodities', { params });
  return response.data;
};

export const getCommodityApi = async (id: number): Promise<CommodityResponse> => {
  const response = await apiClient.get<CommodityResponse>(`/api/v1/commodities/${id}`);
  return response.data;
};

export const getMandisApi = async (state?: string, district?: string): Promise<MandiResponse[]> => {
  const params: Record<string, string> = {};
  if (state && state.trim() !== '') {
    params.state = state.trim();
  }
  if (district && district.trim() !== '') {
    if (!state || state.trim() === '') {
      throw new Error('District filter requires state parameter to be provided.');
    }
    params.district = district.trim();
  }
  const response = await apiClient.get<MandiResponse[]>('/api/v1/mandis', { params });
  return response.data;
};

export const getMandiApi = async (id: number): Promise<MandiResponse> => {
  const response = await apiClient.get<MandiResponse>(`/api/v1/mandis/${id}`);
  return response.data;
};

export const getPriceDiscoveryApi = async (
  commodityId: number,
  priceDate: string
): Promise<PriceDiscoveryResponse> => {
  const response = await apiClient.get<PriceDiscoveryResponse>('/api/v1/market-prices/discovery', {
    params: {
      commodityId,
      priceDate,
    },
  });
  return response.data;
};

export const getMarketPricesApi = async (filters: MarketPriceFilters): Promise<MarketPriceResponse[]> => {
  const params: Record<string, string | number> = {};

  if (filters.commodityId) {
    params.commodityId = filters.commodityId;
  }
  if (filters.mandiId) {
    params.mandiId = filters.mandiId;
  }
  if (filters.priceDate) {
    params.priceDate = filters.priceDate;
  }
  if (filters.startDate) {
    params.startDate = filters.startDate;
  }
  if (filters.endDate) {
    params.endDate = filters.endDate;
  }

  const response = await apiClient.get<MarketPriceResponse[]>('/api/v1/market-prices', { params });
  return response.data;
};

export const recordMarketPriceApi = async (
  data: RecordMarketPriceRequest
): Promise<MarketPriceResponse> => {
  const response = await apiClient.post<MarketPriceResponse>('/api/v1/market-prices', data);
  return response.data;
};
