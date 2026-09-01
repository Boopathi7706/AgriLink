import { useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { getCommoditiesApi, getMandisApi, getPriceDiscoveryApi, getMarketPricesApi } from '@/api/marketPrices';
import { PageContainer } from '@/components/layout/PageContainer';
import { MarketPriceFilters, type FilterMode } from '@/components/marketPrices/MarketPriceFilters';
import { PriceDiscoverySummary } from '@/components/marketPrices/PriceDiscoverySummary';
import { MarketPriceTable } from '@/components/marketPrices/MarketPriceTable';
import { HistoricalPriceChart } from '@/components/marketPrices/HistoricalPriceChart';
import { RecordPriceModal } from '@/components/marketPrices/RecordPriceModal';
import { AlertCircle } from 'lucide-react';
import axios from 'axios';
import type { ErrorResponse } from '@/types/auth';

const getInitialDates = () => {
  const today = new Date();
  const todayStr = today.toISOString().split('T')[0];
  const thirtyDaysAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000);
  const thirtyDaysAgoStr = thirtyDaysAgo.toISOString().split('T')[0];
  return { todayStr, thirtyDaysAgoStr };
};

export const MarketPricesPage = () => {
  const queryClient = useQueryClient();
  const [{ todayStr, thirtyDaysAgoStr }] = useState(getInitialDates);

  const [mode, setMode] = useState<FilterMode>('DAILY');
  const [selectedCommodityId, setSelectedCommodityId] = useState<number | null>(null);
  const [selectedMandiId, setSelectedMandiId] = useState<number | null>(null);
  const [priceDate, setPriceDate] = useState<string>(todayStr);
  const [startDate, setStartDate] = useState<string>(thirtyDaysAgoStr);
  const [endDate, setEndDate] = useState<string>(todayStr);

  // 1. Fetch commodities
  const { data: commodities = [], isLoading: isLoadingCommodities } = useQuery({
    queryKey: ['commodities'],
    queryFn: () => getCommoditiesApi(),
    staleTime: 10 * 60 * 1000,
  });

  // Auto-select first commodity if none selected
  const activeCommodityId = selectedCommodityId ?? (commodities.length > 0 ? commodities[0].id : null);

  // 2. Fetch mandis
  const { data: mandis = [], isLoading: isLoadingMandis } = useQuery({
    queryKey: ['mandis'],
    queryFn: () => getMandisApi(),
    staleTime: 10 * 60 * 1000,
  });

  // 3. Fetch Price Discovery Aggregate (Daily mode)
  const {
    data: priceDiscovery,
    isLoading: isLoadingDiscovery,
    error: discoveryError,
  } = useQuery({
    queryKey: ['priceDiscovery', { commodityId: activeCommodityId, priceDate }],
    queryFn: () => getPriceDiscoveryApi(activeCommodityId!, priceDate),
    enabled: mode === 'DAILY' && Boolean(activeCommodityId && priceDate),
    staleTime: 2 * 60 * 1000,
  });

  // 4. Fetch Historical Prices (Historical mode)
  const {
    data: historicalPrices = [],
    isLoading: isLoadingHistorical,
    error: historicalError,
  } = useQuery({
    queryKey: [
      'marketPrices',
      { commodityId: activeCommodityId, mandiId: selectedMandiId, startDate, endDate },
    ],
    queryFn: () =>
      getMarketPricesApi({
        commodityId: activeCommodityId,
        mandiId: selectedMandiId,
        startDate,
        endDate,
      }),
    enabled: mode === 'HISTORICAL' && Boolean(activeCommodityId && startDate && endDate),
    staleTime: 2 * 60 * 1000,
  });

  const handleResetFilters = () => {
    setSelectedCommodityId(commodities.length > 0 ? commodities[0].id : null);
    setSelectedMandiId(null);
    setPriceDate(todayStr);
    setStartDate(thirtyDaysAgoStr);
    setEndDate(todayStr);
  };

  const handleRecordSuccess = () => {
    queryClient.invalidateQueries({ queryKey: ['priceDiscovery'] });
    queryClient.invalidateQueries({ queryKey: ['marketPrices'] });
  };

  const activeCommodity = commodities.find((c) => c.id === activeCommodityId);

  // Filter discovery mandi prices if mandi filter is selected
  const filteredMandiPrices = selectedMandiId && priceDiscovery?.mandiPrices
    ? priceDiscovery.mandiPrices.filter((p) => p.mandiId === selectedMandiId)
    : priceDiscovery?.mandiPrices ?? [];

  const renderErrorMessage = (err: unknown) => {
    if (!err) return null;
    if (axios.isAxiosError(err) && err.response?.data) {
      const errorData = err.response.data as ErrorResponse;
      return errorData.message || 'Failed to fetch market price data.';
    }
    return 'Failed to load market price data. Please check connection.';
  };

  return (
    <PageContainer
      title="APMC Market Price Discovery"
      description="Real-time agricultural commodity prices, APMC mandi rates, and market trends."
    >
      <div className="space-y-6 max-w-7xl mx-auto w-full">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-xl font-bold text-foreground">Market Price Analytics</h2>
            <p className="text-sm text-muted-foreground">
              Select a commodity and date to view price discovery across government mandis.
            </p>
          </div>

          <RecordPriceModal
            commodities={commodities}
            mandis={mandis}
            onSuccess={handleRecordSuccess}
          />
        </div>

        {/* Filters Bar */}
        <MarketPriceFilters
          mode={mode}
          onModeChange={setMode}
          commodities={commodities}
          mandis={mandis}
          selectedCommodityId={activeCommodityId}
          onCommodityChange={setSelectedCommodityId}
          selectedMandiId={selectedMandiId}
          onMandiChange={setSelectedMandiId}
          priceDate={priceDate}
          onPriceDateChange={setPriceDate}
          startDate={startDate}
          onStartDateChange={setStartDate}
          endDate={endDate}
          onEndDateChange={setEndDate}
          onReset={handleResetFilters}
          isLoading={isLoadingCommodities || isLoadingMandis}
        />

        {/* Error Alert */}
        {(discoveryError || historicalError) && (
          <div className="p-4 rounded-lg bg-destructive/10 text-destructive border border-destructive/20 flex items-center gap-2 text-sm font-medium">
            <AlertCircle className="h-5 w-5 shrink-0" />
            <span>{renderErrorMessage(discoveryError || historicalError)}</span>
          </div>
        )}

        {/* Daily Mode View */}
        {mode === 'DAILY' && (
          <div className="space-y-6">
            <PriceDiscoverySummary
              discovery={priceDiscovery}
              isLoading={isLoadingDiscovery}
            />

            <MarketPriceTable
              prices={filteredMandiPrices}
              isLoading={isLoadingDiscovery}
            />
          </div>
        )}

        {/* Historical Mode View */}
        {mode === 'HISTORICAL' && (
          <HistoricalPriceChart
            prices={historicalPrices}
            commodityName={activeCommodity?.name}
            isLoading={isLoadingHistorical}
          />
        )}
      </div>
    </PageContainer>
  );
};
