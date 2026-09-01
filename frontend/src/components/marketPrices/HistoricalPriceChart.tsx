import { useMemo, useState } from 'react';
import type { MarketPriceResponse } from '@/types/marketPrice';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { TrendingUp, LineChart } from 'lucide-react';

interface HistoricalPriceChartProps {
  prices: MarketPriceResponse[];
  commodityName?: string;
  isLoading?: boolean;
}

export const HistoricalPriceChart = ({ prices, commodityName, isLoading }: HistoricalPriceChartProps) => {
  const [hoveredPoint, setHoveredPoint] = useState<MarketPriceResponse | null>(null);

  // Sort prices chronologically (ascending date)
  const sortedPrices = useMemo(() => {
    if (!prices || prices.length === 0) return [];
    return [...prices].sort((a, b) => new Date(a.priceDate).getTime() - new Date(b.priceDate).getTime());
  }, [prices]);

  const chartData = useMemo(() => {
    if (sortedPrices.length === 0) return null;

    const minPrices = sortedPrices.map((p) => Number(p.minPrice));
    const maxPrices = sortedPrices.map((p) => Number(p.maxPrice));

    const overallMin = Math.min(...minPrices);
    const overallMax = Math.max(...maxPrices);
    const range = overallMax - overallMin || 1;

    const width = 600;
    const height = 240;
    const padding = 30;

    const getX = (index: number) => {
      if (sortedPrices.length === 1) return width / 2;
      return padding + (index / (sortedPrices.length - 1)) * (width - padding * 2);
    };

    const getY = (val: number) => {
      return height - padding - ((val - overallMin) / range) * (height - padding * 2);
    };

    const modalPoints = sortedPrices.map((p, i) => `${getX(i)},${getY(Number(p.modalPrice))}`).join(' ');
    const minPoints = sortedPrices.map((p, i) => `${getX(i)},${getY(Number(p.minPrice))}`).join(' ');
    const maxPoints = sortedPrices.map((p, i) => `${getX(i)},${getY(Number(p.maxPrice))}`).join(' ');

    return {
      width,
      height,
      overallMin,
      overallMax,
      getX,
      getY,
      modalPoints,
      minPoints,
      maxPoints,
    };
  }, [sortedPrices]);

  const formatCurrency = (val: number) => {
    return `₹${val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  };

  if (isLoading) {
    return (
      <Card className="w-full shadow-sm border-border">
        <CardHeader>
          <div className="h-6 bg-muted rounded w-48 animate-pulse mb-2" />
          <div className="h-4 bg-muted rounded w-64 animate-pulse" />
        </CardHeader>
        <CardContent className="p-6">
          <div className="h-60 bg-muted/40 rounded animate-pulse w-full" />
        </CardContent>
      </Card>
    );
  }

  if (!sortedPrices || sortedPrices.length === 0) {
    return (
      <Card className="w-full shadow-sm border-border">
        <CardContent className="p-12 text-center">
          <LineChart className="h-12 w-12 text-muted-foreground mx-auto mb-4 opacity-50" />
          <h3 className="text-lg font-semibold text-foreground mb-1">No Historical Price Data</h3>
          <p className="text-sm text-muted-foreground max-w-md mx-auto">
            Select a commodity and a valid date range to visualize price trends over time.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full shadow-sm border-border">
      <CardHeader>
        <div className="flex items-center justify-between flex-wrap gap-2">
          <div>
            <CardTitle className="text-xl font-bold flex items-center gap-2">
              <TrendingUp className="h-5 w-5 text-primary" />
              <span>Historical Price Trend</span>
            </CardTitle>
            <CardDescription>
              {commodityName ? `Price movement for ${commodityName}` : 'Commodity price movement over time'}
            </CardDescription>
          </div>

          <div className="flex items-center gap-3 text-xs">
            <div className="flex items-center gap-1">
              <span className="h-2.5 w-2.5 rounded-full bg-primary inline-block" />
              <span className="font-medium text-foreground">Modal Price</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="h-2.5 w-2.5 rounded-full bg-emerald-500 inline-block" />
              <span className="text-muted-foreground">Min Price</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="h-2.5 w-2.5 rounded-full bg-amber-500 inline-block" />
              <span className="text-muted-foreground">Max Price</span>
            </div>
          </div>
        </div>
      </CardHeader>

      <CardContent className="space-y-6">
        {/* SVG Chart */}
        {chartData && (
          <div className="w-full overflow-x-auto bg-muted/20 p-4 rounded-xl border">
            <div className="min-w-[600px] relative">
              <svg
                viewBox={`0 0 ${chartData.width} ${chartData.height}`}
                className="w-full h-64 overflow-visible"
              >
                {/* Horizontal Grid lines */}
                {[0, 0.25, 0.5, 0.75, 1].map((ratio, idx) => {
                  const y = chartData.height - 30 - ratio * (chartData.height - 60);
                  const val = chartData.overallMin + ratio * (chartData.overallMax - chartData.overallMin);
                  return (
                    <g key={idx}>
                      <line
                        x1="30"
                        y1={y}
                        x2={chartData.width - 30}
                        y2={y}
                        stroke="currentColor"
                        className="text-border"
                        strokeDasharray="4,4"
                        strokeWidth="1"
                      />
                      <text
                        x="25"
                        y={y + 3}
                        className="text-[10px] fill-muted-foreground"
                        textAnchor="end"
                      >
                        ₹{Math.round(val)}
                      </text>
                    </g>
                  );
                })}

                {/* Min Price Line */}
                <polyline
                  fill="none"
                  stroke="#10b981"
                  strokeWidth="2"
                  strokeDasharray="3,3"
                  points={chartData.minPoints}
                />

                {/* Max Price Line */}
                <polyline
                  fill="none"
                  stroke="#f59e0b"
                  strokeWidth="2"
                  strokeDasharray="3,3"
                  points={chartData.maxPoints}
                />

                {/* Modal Price Line */}
                <polyline
                  fill="none"
                  stroke="var(--primary, #16a34a)"
                  strokeWidth="3"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  points={chartData.modalPoints}
                />

                {/* Data Points */}
                {sortedPrices.map((p, index) => {
                  const cx = chartData.getX(index);
                  const cy = chartData.getY(Number(p.modalPrice));
                  const isHovered = hoveredPoint?.id === p.id;

                  return (
                    <g key={p.id}>
                      <circle
                        cx={cx}
                        cy={cy}
                        r={isHovered ? '6' : '4'}
                        className="fill-primary stroke-background cursor-pointer transition-all"
                        strokeWidth="2"
                        onMouseEnter={() => setHoveredPoint(p)}
                        onMouseLeave={() => setHoveredPoint(null)}
                      />
                      <text
                        x={cx}
                        y={chartData.height - 10}
                        className="text-[9px] fill-muted-foreground font-mono"
                        textAnchor="middle"
                      >
                        {p.priceDate.substring(5)}
                      </text>
                    </g>
                  );
                })}
              </svg>

              {/* Hover Tooltip Overlay */}
              {hoveredPoint && (
                <div className="absolute top-2 right-2 p-3 bg-card border rounded-lg shadow-md text-xs space-y-1 z-10">
                  <p className="font-bold text-foreground">{hoveredPoint.mandiName}</p>
                  <p className="text-muted-foreground">{hoveredPoint.priceDate}</p>
                  <div className="pt-1 border-t flex items-center justify-between gap-4 font-mono">
                    <span>Modal Price:</span>
                    <span className="font-bold text-primary">{formatCurrency(hoveredPoint.modalPrice)}</span>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Timeline Records List */}
        <div className="border rounded-lg overflow-hidden text-sm">
          <div className="bg-muted/40 p-3 font-semibold text-xs text-muted-foreground uppercase border-b">
            Chronological Price History ({sortedPrices.length} Records)
          </div>
          <div className="divide-y max-h-60 overflow-y-auto">
            {sortedPrices.map((p) => (
              <div key={p.id} className="p-3 flex items-center justify-between hover:bg-muted/20 text-xs sm:text-sm">
                <div>
                  <span className="font-medium text-foreground">{p.priceDate}</span>
                  <span className="text-muted-foreground ml-2 text-xs">({p.mandiName})</span>
                </div>
                <div className="flex items-center gap-3 font-mono">
                  <span className="text-xs text-muted-foreground hidden sm:inline">Min: {formatCurrency(p.minPrice)}</span>
                  <span className="text-xs text-muted-foreground hidden sm:inline">Max: {formatCurrency(p.maxPrice)}</span>
                  <Badge variant="outline" className="font-bold text-primary">
                    Modal: {formatCurrency(p.modalPrice)}
                  </Badge>
                </div>
              </div>
            ))}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
