import type { CommodityResponse, MandiResponse } from '@/types/marketPrice';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent } from '@/components/ui/card';
import { RotateCcw, Calendar, Filter } from 'lucide-react';

export type FilterMode = 'DAILY' | 'HISTORICAL';

interface MarketPriceFiltersProps {
  mode: FilterMode;
  onModeChange: (mode: FilterMode) => void;
  commodities: CommodityResponse[];
  mandis: MandiResponse[];
  selectedCommodityId: number | null;
  onCommodityChange: (id: number | null) => void;
  selectedMandiId: number | null;
  onMandiChange: (id: number | null) => void;
  priceDate: string;
  onPriceDateChange: (date: string) => void;
  startDate: string;
  onStartDateChange: (date: string) => void;
  endDate: string;
  onEndDateChange: (date: string) => void;
  onReset: () => void;
  isLoading?: boolean;
}

export const MarketPriceFilters = ({
  mode,
  onModeChange,
  commodities,
  mandis,
  selectedCommodityId,
  onCommodityChange,
  selectedMandiId,
  onMandiChange,
  priceDate,
  onPriceDateChange,
  startDate,
  onStartDateChange,
  endDate,
  onEndDateChange,
  onReset,
  isLoading,
}: MarketPriceFiltersProps) => {
  return (
    <Card className="w-full shadow-sm border-border">
      <CardContent className="p-6 space-y-6">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b pb-4">
          <div className="flex items-center space-x-2">
            <Filter className="h-5 w-5 text-primary" />
            <h3 className="font-semibold text-lg text-foreground">Filter Market Prices</h3>
          </div>

          <div className="flex items-center bg-muted p-1 rounded-lg border border-border">
            <button
              type="button"
              onClick={() => onModeChange('DAILY')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-md transition-colors ${
                mode === 'DAILY'
                  ? 'bg-background text-foreground shadow-sm'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              Daily Discovery
            </button>
            <button
              type="button"
              onClick={() => onModeChange('HISTORICAL')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-md transition-colors ${
                mode === 'HISTORICAL'
                  ? 'bg-background text-foreground shadow-sm'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              Historical Trends
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Commodity Select */}
          <div className="space-y-2">
            <Label htmlFor="commoditySelect" className="after:content-['*'] after:ml-0.5 after:text-destructive">
              Commodity
            </Label>
            <select
              id="commoditySelect"
              className="flex h-9 w-full rounded-md border border-input bg-background px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
              value={selectedCommodityId ?? ''}
              onChange={(e) => onCommodityChange(e.target.value ? Number(e.target.value) : null)}
              disabled={isLoading}
            >
              <option value="">-- Select Commodity --</option>
              {commodities.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} ({c.category})
                </option>
              ))}
            </select>
          </div>

          {/* Mandi Select */}
          <div className="space-y-2">
            <Label htmlFor="mandiSelect">Mandi / Market (Optional)</Label>
            <select
              id="mandiSelect"
              className="flex h-9 w-full rounded-md border border-input bg-background px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
              value={selectedMandiId ?? ''}
              onChange={(e) => onMandiChange(e.target.value ? Number(e.target.value) : null)}
              disabled={isLoading}
            >
              <option value="">-- All Mandis --</option>
              {mandis.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.name} - {m.district}, {m.state}
                </option>
              ))}
            </select>
          </div>

          {/* Date Picker Mode */}
          {mode === 'DAILY' ? (
            <div className="space-y-2">
              <Label htmlFor="priceDateInput" className="flex items-center gap-1.5">
                <Calendar className="h-4 w-4 text-muted-foreground" />
                <span>Price Date</span>
              </Label>
              <Input
                id="priceDateInput"
                type="date"
                value={priceDate}
                onChange={(e) => onPriceDateChange(e.target.value)}
                disabled={isLoading}
              />
            </div>
          ) : (
            <div className="grid grid-cols-2 gap-2">
              <div className="space-y-2">
                <Label htmlFor="startDateInput">Start Date</Label>
                <Input
                  id="startDateInput"
                  type="date"
                  value={startDate}
                  onChange={(e) => onStartDateChange(e.target.value)}
                  disabled={isLoading}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="endDateInput">End Date</Label>
                <Input
                  id="endDateInput"
                  type="date"
                  value={endDate}
                  onChange={(e) => onEndDateChange(e.target.value)}
                  disabled={isLoading}
                />
              </div>
            </div>
          )}
        </div>

        <div className="flex items-center justify-end border-t pt-4">
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={onReset}
            disabled={isLoading}
            className="flex items-center gap-1.5"
          >
            <RotateCcw className="h-3.5 w-3.5" />
            <span>Reset Filters</span>
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};
