import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import type { BuyerProfileResponse, UpdateBuyerProfileRequest, BuyerType } from '@/types/profile';
import { BUYER_TYPE_OPTIONS } from '@/types/profile';
import { updateBuyerProfileApi } from '@/api/profile';
import { useAuthStore } from '@/store/useAuthStore';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Loader2, CheckCircle, AlertCircle, Save, RotateCcw } from 'lucide-react';
import type { ErrorResponse } from '@/types/auth';
import axios from 'axios';

const buyerProfileSchema = z.object({
  businessName: z.string().min(1, 'Business name is required').max(150, 'Business name must not exceed 150 characters'),
  buyerType: z.enum(['WHOLESALER', 'RETAILER', 'PROCESSOR', 'EXPORTER', 'INDIVIDUAL'] as const, {
    errorMap: () => ({ message: 'Please select a valid buyer type' }),
  }),
  gstin: z.string().max(20, 'GSTIN must not exceed 20 characters').optional().or(z.literal('')),
  address: z.string().max(255, 'Address must not exceed 255 characters').optional().or(z.literal('')),
  district: z.string().min(1, 'District is required').max(100, 'District must not exceed 100 characters'),
  state: z.string().min(1, 'State is required').max(100, 'State must not exceed 100 characters'),
  pincode: z.string().max(10, 'Pincode must not exceed 10 characters').optional().or(z.literal('')),
});

type BuyerFormValues = z.infer<typeof buyerProfileSchema>;

interface BuyerProfileFormProps {
  profile: BuyerProfileResponse | null | undefined;
}

export const BuyerProfileForm = ({ profile }: BuyerProfileFormProps) => {
  const [isLoading, setIsLoading] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const { user, setUser } = useAuthStore();

  const getDefaultValues = (p?: BuyerProfileResponse | null): BuyerFormValues => ({
    businessName: p?.businessName ?? '',
    buyerType: (p?.buyerType as BuyerType) ?? 'WHOLESALER',
    gstin: p?.gstin ?? '',
    address: p?.address ?? '',
    district: p?.district ?? '',
    state: p?.state ?? '',
    pincode: p?.pincode ?? '',
  });

  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isDirty },
  } = useForm<BuyerFormValues>({
    resolver: zodResolver(buyerProfileSchema),
    defaultValues: getDefaultValues(profile),
  });

  useEffect(() => {
    if (profile) {
      reset(getDefaultValues(profile));
    }
  }, [profile, reset]);

  const onSubmit = async (data: BuyerFormValues) => {
    setIsLoading(true);
    setServerError(null);
    setSuccessMessage(null);

    const payload: UpdateBuyerProfileRequest = {
      businessName: data.businessName.trim(),
      buyerType: data.buyerType,
      gstin: data.gstin ? data.gstin.trim() : undefined,
      address: data.address ? data.address.trim() : undefined,
      district: data.district.trim(),
      state: data.state.trim(),
      pincode: data.pincode ? data.pincode.trim() : undefined,
    };

    try {
      const updatedProfile = await updateBuyerProfileApi(payload);

      // Update local auth store without losing user metadata
      if (user) {
        setUser({
          ...user,
          buyerProfile: updatedProfile,
        });
      }

      setSuccessMessage('Buyer profile updated successfully.');
      reset(getDefaultValues(updatedProfile));
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const status = err.response.status;
        const errorData = err.response.data as ErrorResponse;

        if (status === 400 && errorData.validationErrors) {
          Object.entries(errorData.validationErrors).forEach(([field, message]) => {
            setError(field as keyof BuyerFormValues, { type: 'server', message });
          });
          setServerError('Please fix the validation errors below.');
        } else if (status === 403) {
          setServerError('Access denied: Only buyers can update a buyer profile.');
        } else if (status === 404) {
          setServerError('Buyer profile record not found.');
        } else {
          setServerError(errorData.message || 'Failed to update profile. Please try again.');
        }
      } else {
        setServerError('Failed to update profile. Please check your network connection.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = () => {
    reset(getDefaultValues(profile));
    setServerError(null);
    setSuccessMessage(null);
  };

  return (
    <Card className="w-full shadow-sm border-border">
      <CardHeader>
        <CardTitle className="text-xl font-bold">Buyer Profile Details</CardTitle>
        <CardDescription>
          Update your business information, buyer type, and address details.
        </CardDescription>
      </CardHeader>

      <form onSubmit={handleSubmit(onSubmit)}>
        <CardContent className="space-y-6">
          {serverError && (
            <div className="flex items-center gap-2 p-4 text-sm rounded-lg bg-destructive/10 text-destructive border border-destructive/20 font-medium">
              <AlertCircle className="h-5 w-5 shrink-0" />
              <span>{serverError}</span>
            </div>
          )}

          {successMessage && (
            <div className="flex items-center gap-2 p-4 text-sm rounded-lg bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-800 font-medium">
              <CheckCircle className="h-5 w-5 shrink-0" />
              <span>{successMessage}</span>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <Label htmlFor="businessName" className="after:content-['*'] after:ml-0.5 after:text-destructive">
                Business / Company Name
              </Label>
              <Input
                id="businessName"
                type="text"
                placeholder="e.g. Agro Traders Ltd"
                maxLength={150}
                disabled={isLoading}
                {...register('businessName')}
              />
              {errors.businessName && (
                <p className="text-xs text-destructive">{errors.businessName.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="buyerType" className="after:content-['*'] after:ml-0.5 after:text-destructive">
                Buyer Classification Type
              </Label>
              <select
                id="buyerType"
                className="flex h-9 w-full rounded-md border border-input bg-background px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
                disabled={isLoading}
                {...register('buyerType')}
              >
                {BUYER_TYPE_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
              {errors.buyerType && (
                <p className="text-xs text-destructive">{errors.buyerType.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="gstin">GSTIN / Business Registration Number</Label>
              <Input
                id="gstin"
                type="text"
                placeholder="e.g. 33ABCDE1234F1Z5"
                maxLength={20}
                disabled={isLoading}
                {...register('gstin')}
              />
              {errors.gstin && (
                <p className="text-xs text-destructive">{errors.gstin.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="address">Business Address</Label>
              <Input
                id="address"
                type="text"
                placeholder="e.g. 45 APMC Mandi Complex"
                maxLength={255}
                disabled={isLoading}
                {...register('address')}
              />
              {errors.address && (
                <p className="text-xs text-destructive">{errors.address.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="district" className="after:content-['*'] after:ml-0.5 after:text-destructive">
                District
              </Label>
              <Input
                id="district"
                type="text"
                placeholder="e.g. Coimbatore"
                maxLength={100}
                disabled={isLoading}
                {...register('district')}
              />
              {errors.district && (
                <p className="text-xs text-destructive">{errors.district.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="state" className="after:content-['*'] after:ml-0.5 after:text-destructive">
                State
              </Label>
              <Input
                id="state"
                type="text"
                placeholder="e.g. Tamil Nadu"
                maxLength={100}
                disabled={isLoading}
                {...register('state')}
              />
              {errors.state && (
                <p className="text-xs text-destructive">{errors.state.message}</p>
              )}
            </div>

            <div className="space-y-2 md:col-span-2">
              <Label htmlFor="pincode">Pincode</Label>
              <Input
                id="pincode"
                type="text"
                placeholder="e.g. 641001"
                maxLength={10}
                disabled={isLoading}
                {...register('pincode')}
              />
              {errors.pincode && (
                <p className="text-xs text-destructive">{errors.pincode.message}</p>
              )}
            </div>
          </div>
        </CardContent>

        <CardFooter className="flex items-center justify-between border-t p-6">
          <Button
            type="button"
            variant="outline"
            onClick={handleReset}
            disabled={isLoading || !isDirty}
            className="flex items-center gap-1.5"
          >
            <RotateCcw className="h-4 w-4" />
            <span>Cancel</span>
          </Button>

          <Button type="submit" disabled={isLoading} className="flex items-center gap-1.5 min-w-[120px]">
            {isLoading ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                <span>Saving...</span>
              </>
            ) : (
              <>
                <Save className="h-4 w-4" />
                <span>Save Changes</span>
              </>
            )}
          </Button>
        </CardFooter>
      </form>
    </Card>
  );
};
