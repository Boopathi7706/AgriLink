import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import type { CommodityResponse, MandiResponse, RecordMarketPriceRequest } from '@/types/marketPrice';
import { recordMarketPriceApi } from '@/api/marketPrices';
import { useAuthStore } from '@/store/useAuthStore';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { PlusCircle, Loader2, AlertCircle, CheckCircle } from 'lucide-react';
import type { ErrorResponse } from '@/types/auth';
import axios from 'axios';

const recordPriceSchema = z
  .object({
    mandiId: z.number({ invalid_type_error: 'Please select a mandi' }).positive('Mandi is required'),
    commodityId: z.number({ invalid_type_error: 'Please select a commodity' }).positive('Commodity is required'),
    minPrice: z.number({ invalid_type_error: 'Minimum price must be a valid number' }).min(0, 'Minimum price must be non-negative'),
    maxPrice: z.number({ invalid_type_error: 'Maximum price must be a valid number' }).min(0, 'Maximum price must be non-negative'),
    modalPrice: z.number({ invalid_type_error: 'Modal price must be a valid number' }).min(0, 'Modal price must be non-negative'),
    priceDate: z.string().min(1, 'Price date is required'),
  })
  .refine((data) => data.maxPrice >= data.minPrice, {
    message: 'Maximum price must be greater than or equal to minimum price',
    path: ['maxPrice'],
  })
  .refine((data) => data.modalPrice >= data.minPrice && data.modalPrice <= data.maxPrice, {
    message: 'Modal price must be between minimum price and maximum price',
    path: ['modalPrice'],
  });

type RecordPriceFormValues = z.infer<typeof recordPriceSchema>;

interface RecordPriceModalProps {
  commodities: CommodityResponse[];
  mandis: MandiResponse[];
  onSuccess?: () => void;
}

export const RecordPriceModal = ({ commodities, mandis, onSuccess }: RecordPriceModalProps) => {
  const [open, setOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const { user, isAuthenticated } = useAuthStore();

  const canRecord = isAuthenticated && user && (user.role === 'FARMER' || user.role === 'ADMIN');

  const today = new Date().toISOString().split('T')[0];

  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors },
  } = useForm<RecordPriceFormValues>({
    resolver: zodResolver(recordPriceSchema),
    defaultValues: {
      mandiId: mandis.length > 0 ? mandis[0].id : 0,
      commodityId: commodities.length > 0 ? commodities[0].id : 0,
      minPrice: 0,
      maxPrice: 0,
      modalPrice: 0,
      priceDate: today,
    },
  });

  if (!canRecord) {
    return null;
  }

  const onSubmit = async (data: RecordPriceFormValues) => {
    setIsLoading(true);
    setServerError(null);
    setSuccessMsg(null);

    const payload: RecordMarketPriceRequest = {
      mandiId: data.mandiId,
      commodityId: data.commodityId,
      minPrice: data.minPrice,
      maxPrice: data.maxPrice,
      modalPrice: data.modalPrice,
      priceDate: data.priceDate,
    };

    try {
      await recordMarketPriceApi(payload);
      setSuccessMsg('Market price record saved successfully.');
      reset();

      if (onSuccess) {
        onSuccess();
      }

      setTimeout(() => {
        setOpen(false);
        setSuccessMsg(null);
      }, 1500);
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const status = err.response.status;
        const errorData = err.response.data as ErrorResponse;

        if (status === 409) {
          setServerError('A price entry already exists for this mandi, commodity, and date.');
        } else if (status === 400 && errorData.validationErrors) {
          Object.entries(errorData.validationErrors).forEach(([field, msg]) => {
            setError(field as keyof RecordPriceFormValues, { type: 'server', message: msg });
          });
          setServerError('Please fix the validation errors.');
        } else {
          setServerError(errorData.message || 'Failed to record market price.');
        }
      } else {
        setServerError('Network error. Unable to connect to backend.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Button onClick={() => setOpen(true)} className="flex items-center gap-1.5 shadow-sm">
        <PlusCircle className="h-4 w-4" />
        <span>Record Daily Price</span>
      </Button>

      {open && (
        <Dialog>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Record Market Price Entry</DialogTitle>
              <DialogDescription>
                Submit daily APMC mandi market price rates for a commodity.
              </DialogDescription>
            </DialogHeader>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 py-2">
              {serverError && (
                <div className="flex items-center gap-2 p-3 text-xs rounded-md bg-destructive/10 text-destructive border border-destructive/20 font-medium">
                  <AlertCircle className="h-4 w-4 shrink-0" />
                  <span>{serverError}</span>
                </div>
              )}

              {successMsg && (
                <div className="flex items-center gap-2 p-3 text-xs rounded-md bg-emerald-50 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-400 border border-emerald-200 font-medium">
                  <CheckCircle className="h-4 w-4 shrink-0" />
                  <span>{successMsg}</span>
                </div>
              )}

              <div className="space-y-2">
                <Label htmlFor="mandiIdSelect">Mandi / Market</Label>
                <select
                  id="mandiIdSelect"
                  className="flex h-9 w-full rounded-md border border-input bg-background px-3 py-1 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                  disabled={isLoading}
                  {...register('mandiId', { valueAsNumber: true })}
                >
                  {mandis.map((m) => (
                    <option key={m.id} value={m.id}>
                      {m.name} ({m.district}, {m.state})
                    </option>
                  ))}
                </select>
                {errors.mandiId && <p className="text-xs text-destructive">{errors.mandiId.message}</p>}
              </div>

              <div className="space-y-2">
                <Label htmlFor="commodityIdSelect">Commodity</Label>
                <select
                  id="commodityIdSelect"
                  className="flex h-9 w-full rounded-md border border-input bg-background px-3 py-1 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                  disabled={isLoading}
                  {...register('commodityId', { valueAsNumber: true })}
                >
                  {commodities.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name} ({c.category})
                    </option>
                  ))}
                </select>
                {errors.commodityId && <p className="text-xs text-destructive">{errors.commodityId.message}</p>}
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div className="space-y-1.5">
                  <Label htmlFor="minPrice">Min Price (₹)</Label>
                  <Input
                    id="minPrice"
                    type="number"
                    step="0.01"
                    min="0"
                    disabled={isLoading}
                    {...register('minPrice', { valueAsNumber: true })}
                  />
                  {errors.minPrice && <p className="text-[10px] text-destructive">{errors.minPrice.message}</p>}
                </div>

                <div className="space-y-1.5">
                  <Label htmlFor="maxPrice">Max Price (₹)</Label>
                  <Input
                    id="maxPrice"
                    type="number"
                    step="0.01"
                    min="0"
                    disabled={isLoading}
                    {...register('maxPrice', { valueAsNumber: true })}
                  />
                  {errors.maxPrice && <p className="text-[10px] text-destructive">{errors.maxPrice.message}</p>}
                </div>

                <div className="space-y-1.5">
                  <Label htmlFor="modalPrice">Modal Price (₹)</Label>
                  <Input
                    id="modalPrice"
                    type="number"
                    step="0.01"
                    min="0"
                    disabled={isLoading}
                    {...register('modalPrice', { valueAsNumber: true })}
                  />
                  {errors.modalPrice && <p className="text-[10px] text-destructive">{errors.modalPrice.message}</p>}
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="recordPriceDate">Price Date</Label>
                <Input
                  id="recordPriceDate"
                  type="date"
                  max={today}
                  disabled={isLoading}
                  {...register('priceDate')}
                />
                {errors.priceDate && <p className="text-xs text-destructive">{errors.priceDate.message}</p>}
              </div>

              <DialogFooter>
                <Button type="button" variant="outline" onClick={() => setOpen(false)} disabled={isLoading}>
                  Cancel
                </Button>
                <Button type="submit" disabled={isLoading} className="min-w-[100px]">
                  {isLoading ? <Loader2 className="h-4 w-4 animate-spin" /> : 'Save Entry'}
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>
      )}
    </>
  );
};
