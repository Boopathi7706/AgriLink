export interface CommodityResponse {
  id: number;
  name: string;
  category: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface MandiResponse {
  id: number;
  name: string;
  district: string;
  state: string;
  latitude?: number;
  longitude?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface MarketPriceResponse {
  id: number;
  mandiId: number;
  mandiName: string;
  district: string;
  state: string;
  commodityId: number;
  commodityName: string;
  category: string;
  minPrice: number;
  maxPrice: number;
  modalPrice: number;
  priceDate: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface PriceDiscoveryResponse {
  commodityId: number;
  commodityName: string;
  category: string;
  priceDate: string;
  lowestModalPrice?: number | null;
  highestModalPrice?: number | null;
  averageModalPrice?: number | null;
  totalMandisReporting: number;
  mandiPrices: MarketPriceResponse[];
}

export interface RecordMarketPriceRequest {
  mandiId: number;
  commodityId: number;
  minPrice: number;
  maxPrice: number;
  modalPrice: number;
  priceDate: string;
}

export interface CreateCommodityRequest {
  name: string;
  category: string;
}

export interface CreateMandiRequest {
  name: string;
  district: string;
  state: string;
  latitude?: number;
  longitude?: number;
}

export interface MarketPriceFilters {
  commodityId?: number | null;
  mandiId?: number | null;
  priceDate?: string;
  startDate?: string;
  endDate?: string;
  state?: string;
  district?: string;
  category?: string;
}
