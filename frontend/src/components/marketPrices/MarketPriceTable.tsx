import type { MarketPriceResponse } from '@/types/marketPrice';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { MapPin, Calendar, Building2 } from 'lucide-react';

interface MarketPriceTableProps {
  prices: MarketPriceResponse[];
  isLoading?: boolean;
}

export const MarketPriceTable = ({ prices, isLoading }: MarketPriceTableProps) => {
  const formatCurrency = (val: number) => {
    return `₹${val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  };

  if (isLoading) {
    return (
      <Card className="w-full shadow-sm border-border">
        <CardHeader>
          <div className="h-6 bg-muted rounded w-48 animate-pulse mb-2" />
          <div className="h-4 bg-muted rounded w-72 animate-pulse" />
        </CardHeader>
        <CardContent className="p-6">
          <div className="space-y-4">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-12 bg-muted rounded animate-pulse" />
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  if (!prices || prices.length === 0) {
    return (
      <Card className="w-full shadow-sm border-border">
        <CardContent className="p-12 text-center">
          <Building2 className="h-12 w-12 text-muted-foreground mx-auto mb-4 opacity-50" />
          <h3 className="text-lg font-semibold text-foreground mb-1">No Market Price Records Found</h3>
          <p className="text-sm text-muted-foreground max-w-md mx-auto">
            No prices were reported for the selected commodity or mandi on this date. Try selecting a different date or commodity.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full shadow-sm border-border">
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="text-xl font-bold">Mandi Daily Price Breakdown</CardTitle>
            <CardDescription>
              Detailed daily commodity prices across reporting APMC mandis.
            </CardDescription>
          </div>
          <Badge variant="outline" className="text-xs">
            {prices.length} {prices.length === 1 ? 'Record' : 'Records'}
          </Badge>
        </div>
      </CardHeader>

      <CardContent className="p-0 sm:p-6">
        {/* Desktop Table View */}
        <div className="hidden md:block overflow-x-auto border rounded-lg">
          <table className="w-full text-sm text-left">
            <thead className="bg-muted/50 text-muted-foreground uppercase text-xs border-b">
              <tr>
                <th className="px-4 py-3 font-semibold">Mandi / Market</th>
                <th className="px-4 py-3 font-semibold">Location</th>
                <th className="px-4 py-3 font-semibold">Commodity</th>
                <th className="px-4 py-3 font-semibold text-right">Min Price</th>
                <th className="px-4 py-3 font-semibold text-right">Max Price</th>
                <th className="px-4 py-3 font-semibold text-right text-primary">Modal Price</th>
                <th className="px-4 py-3 font-semibold text-center">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {prices.map((p) => (
                <tr key={p.id} className="hover:bg-muted/30 transition-colors">
                  <td className="px-4 py-3.5 font-medium text-foreground">
                    <div className="flex items-center gap-2">
                      <Building2 className="h-4 w-4 text-primary shrink-0" />
                      <span>{p.mandiName}</span>
                    </div>
                  </td>
                  <td className="px-4 py-3.5 text-muted-foreground">
                    <div className="flex items-center gap-1.5 text-xs">
                      <MapPin className="h-3.5 w-3.5 shrink-0" />
                      <span>
                        {p.district}, {p.state}
                      </span>
                    </div>
                  </td>
                  <td className="px-4 py-3.5">
                    <span className="font-medium text-foreground">{p.commodityName}</span>
                    <span className="text-xs text-muted-foreground block">{p.category}</span>
                  </td>
                  <td className="px-4 py-3.5 text-right font-mono text-emerald-600 dark:text-emerald-400">
                    {formatCurrency(p.minPrice)}
                  </td>
                  <td className="px-4 py-3.5 text-right font-mono text-amber-600 dark:text-amber-400">
                    {formatCurrency(p.maxPrice)}
                  </td>
                  <td className="px-4 py-3.5 text-right font-mono font-bold text-primary text-base">
                    {formatCurrency(p.modalPrice)}
                  </td>
                  <td className="px-4 py-3.5 text-center text-xs text-muted-foreground">
                    <div className="flex items-center justify-center gap-1">
                      <Calendar className="h-3.5 w-3.5" />
                      <span>{p.priceDate}</span>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Mobile Cards View */}
        <div className="md:hidden space-y-4 p-4">
          {prices.map((p) => (
            <div key={p.id} className="p-4 rounded-lg border bg-card space-y-3 shadow-xs">
              <div className="flex items-start justify-between">
                <div>
                  <h4 className="font-bold text-foreground flex items-center gap-1.5">
                    <Building2 className="h-4 w-4 text-primary" />
                    <span>{p.mandiName}</span>
                  </h4>
                  <p className="text-xs text-muted-foreground flex items-center gap-1 mt-0.5">
                    <MapPin className="h-3 w-3" />
                    <span>
                      {p.district}, {p.state}
                    </span>
                  </p>
                </div>
                <Badge variant="secondary" className="text-xs">
                  {p.commodityName}
                </Badge>
              </div>

              <div className="grid grid-cols-3 gap-2 bg-muted/40 p-3 rounded-md text-center border">
                <div>
                  <span className="text-[10px] text-muted-foreground uppercase block font-medium">Min</span>
                  <span className="text-xs font-mono font-semibold text-emerald-600 dark:text-emerald-400">
                    {formatCurrency(p.minPrice)}
                  </span>
                </div>
                <div>
                  <span className="text-[10px] text-muted-foreground uppercase block font-medium">Max</span>
                  <span className="text-xs font-mono font-semibold text-amber-600 dark:text-amber-400">
                    {formatCurrency(p.maxPrice)}
                  </span>
                </div>
                <div>
                  <span className="text-[10px] text-muted-foreground uppercase block font-medium">Modal</span>
                  <span className="text-sm font-mono font-bold text-primary">
                    {formatCurrency(p.modalPrice)}
                  </span>
                </div>
              </div>

              <div className="flex items-center justify-between text-xs text-muted-foreground pt-1 border-t">
                <span>Date: {p.priceDate}</span>
                <span className="text-[11px] text-muted-foreground">{p.category}</span>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};
