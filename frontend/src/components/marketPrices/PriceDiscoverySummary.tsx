import type { PriceDiscoveryResponse } from '@/types/marketPrice';
import { Card, CardContent } from '@/components/ui/card';
import { TrendingDown, TrendingUp, BarChart3, Building2 } from 'lucide-react';

interface PriceDiscoverySummaryProps {
  discovery: PriceDiscoveryResponse | null | undefined;
  isLoading?: boolean;
}

export const PriceDiscoverySummary = ({ discovery, isLoading }: PriceDiscoverySummaryProps) => {
  const formatCurrency = (val?: number | null) => {
    if (val === undefined || val === null) return 'N/A';
    return `₹${val.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} / Qtl`;
  };

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 w-full">
        {[1, 2, 3, 4].map((i) => (
          <Card key={i} className="animate-pulse shadow-sm">
            <CardContent className="p-6">
              <div className="h-4 bg-muted rounded w-24 mb-3" />
              <div className="h-8 bg-muted rounded w-32 mb-1" />
              <div className="h-3 bg-muted rounded w-20" />
            </CardContent>
          </Card>
        ))}
      </div>
    );
  }

  const lowest = discovery?.lowestModalPrice;
  const highest = discovery?.highestModalPrice;
  const average = discovery?.averageModalPrice;
  const totalMandis = discovery?.totalMandisReporting ?? 0;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 w-full">
      {/* Lowest Modal Price */}
      <Card className="shadow-sm border-border bg-card">
        <CardContent className="p-6 flex items-start justify-between">
          <div>
            <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
              Lowest Modal Price
            </p>
            <h3 className="text-2xl font-bold text-emerald-600 dark:text-emerald-400 mt-1">
              {formatCurrency(lowest)}
            </h3>
            <p className="text-xs text-muted-foreground mt-1">Minimum across APMC mandis</p>
          </div>
          <div className="p-2.5 rounded-xl bg-emerald-100/50 dark:bg-emerald-950/40 text-emerald-600 dark:text-emerald-400 shrink-0">
            <TrendingDown className="h-5 w-5" />
          </div>
        </CardContent>
      </Card>

      {/* Highest Modal Price */}
      <Card className="shadow-sm border-border bg-card">
        <CardContent className="p-6 flex items-start justify-between">
          <div>
            <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
              Highest Modal Price
            </p>
            <h3 className="text-2xl font-bold text-amber-600 dark:text-amber-400 mt-1">
              {formatCurrency(highest)}
            </h3>
            <p className="text-xs text-muted-foreground mt-1">Maximum across APMC mandis</p>
          </div>
          <div className="p-2.5 rounded-xl bg-amber-100/50 dark:bg-amber-950/40 text-amber-600 dark:text-amber-400 shrink-0">
            <TrendingUp className="h-5 w-5" />
          </div>
        </CardContent>
      </Card>

      {/* Average Modal Price */}
      <Card className="shadow-sm border-border bg-card">
        <CardContent className="p-6 flex items-start justify-between">
          <div>
            <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
              Average Modal Price
            </p>
            <h3 className="text-2xl font-bold text-primary mt-1">
              {formatCurrency(average)}
            </h3>
            <p className="text-xs text-muted-foreground mt-1">Mean price across mandis</p>
          </div>
          <div className="p-2.5 rounded-xl bg-primary/10 text-primary shrink-0">
            <BarChart3 className="h-5 w-5" />
          </div>
        </CardContent>
      </Card>

      {/* Total Mandis Reporting */}
      <Card className="shadow-sm border-border bg-card">
        <CardContent className="p-6 flex items-start justify-between">
          <div>
            <p className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
              Mandis Reporting
            </p>
            <h3 className="text-2xl font-bold text-foreground mt-1">
              {totalMandis} {totalMandis === 1 ? 'Mandi' : 'Mandis'}
            </h3>
            <p className="text-xs text-muted-foreground mt-1">Reporting on selected date</p>
          </div>
          <div className="p-2.5 rounded-xl bg-blue-100/50 dark:bg-blue-950/40 text-blue-600 dark:text-blue-400 shrink-0">
            <Building2 className="h-5 w-5" />
          </div>
        </CardContent>
      </Card>
    </div>
  );
};
