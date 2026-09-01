import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import type { FarmerProfileResponse, UpdateFarmerProfileRequest } from '@/types/profile';
import { updateFarmerProfileApi } from '@/api/profile';
import { useAuthStore } from '@/store/useAuthStore';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Loader2, CheckCircle, AlertCircle, Save, RotateCcw } from 'lucide-react';
import type { ErrorResponse } from '@/types/auth';
import axios from 'axios';

const farmerProfileSchema = z.object({
  farmSizeAcres: z
    .number({ invalid_type_error: 'Farm size must be a valid number' })
    .min(0, 'Farm size must be non-negative')
    .optional()
    .or(z.nan().transform(() => undefined)),
  village: z.string().max(100, 'Village name must not exceed 100 characters').optional().or(z.literal('')),
  district: z.string().min(1, 'District is required').max(100, 'District must not exceed 100 characters'),
  state: z.string().min(1, 'State is required').max(100, 'State must not exceed 100 characters'),
  pincode: z.string().max(10, 'Pincode must not exceed 10 characters').optional().or(z.literal('')),
  primaryCrops: z.string().max(255, 'Primary crops must not exceed 255 characters').optional().or(z.literal('')),
});

type FarmerFormValues = z.infer<typeof farmerProfileSchema>;

interface FarmerProfileFormProps {
  profile: FarmerProfileResponse | null | undefined;
}

export const FarmerProfileForm = ({ profile }: FarmerProfileFormProps) => {
  const [isLoading, setIsLoading] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const { user, setUser } = useAuthStore();

  const getDefaultValues = (p?: FarmerProfileResponse | null): FarmerFormValues => ({
    farmSizeAcres: p?.farmSizeAcres ?? undefined,
    village: p?.village ?? '',
    district: p?.district ?? '',
    state: p?.state ?? '',
    pincode: p?.pincode ?? '',
    primaryCrops: p?.primaryCrops ?? '',
  });

  const {
    register,
    handleSubmit,
    reset,
    setError,
    formState: { errors, isDirty },
  } = useForm<FarmerFormValues>({
    resolver: zodResolver(farmerProfileSchema),
    defaultValues: getDefaultValues(profile),
  });

  useEffect(() => {
    if (profile) {
      reset(getDefaultValues(profile));
    }
  }, [profile, reset]);

  const onSubmit = async (data: FarmerFormValues) => {
    setIsLoading(true);
    setServerError(null);
    setSuccessMessage(null);

    const payload: UpdateFarmerProfileRequest = {
      farmSizeAcres: data.farmSizeAcres !== undefined && !Number.isNaN(data.farmSizeAcres) ? data.farmSizeAcres : undefined,
      village: data.village ? data.village.trim() : undefined,
      district: data.district.trim(),
      state: data.state.trim(),
      pincode: data.pincode ? data.pincode.trim() : undefined,
      primaryCrops: data.primaryCrops ? data.primaryCrops.trim() : undefined,
    };

    try {
      const updatedProfile = await updateFarmerProfileApi(payload);
      
      if (user) {
        setUser({
          ...user,
          farmerProfile: updatedProfile,
        });
      }

      setSuccessMessage('Farmer profile updated successfully.');
      reset(getDefaultValues(updatedProfile));
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const status = err.response.status;
        const errorData = err.response.data as ErrorResponse;

        if (status === 400 && errorData.validationErrors) {
          Object.entries(errorData.validationErrors).forEach(([field, message]) => {
            setError(field as keyof FarmerFormValues, { type: 'server', message });
          });
          setServerError('Please fix the validation errors below.');
        } else if (status === 403) {
          setServerError('Access denied: Only farmers can update a farmer profile.');
        } else if (status === 404) {
          setServerError('Farmer profile record not found.');
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
        <CardTitle className="text-xl font-bold">Farmer Profile Details</CardTitle>
        <CardDescription>
          Update your farm size, location, and crop details to connect with buyers.
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
              <Label htmlFor="farmSizeAcres">
                Farm Size (Acres)
              </Label>
              <Input
                id="farmSizeAcres"
                type="number"
                step="0.01"
                min="0"
                placeholder="e.g. 5.5"
                disabled={isLoading}
                {...register('farmSizeAcres', { valueAsNumber: true })}
              />
              {errors.farmSizeAcres && (
                <p className="text-xs text-destructive">{errors.farmSizeAcres.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="village">Village / Locality</Label>
              <Input
                id="village"
                type="text"
                placeholder="e.g. Vadugapatti"
                maxLength={100}
                disabled={isLoading}
                {...register('village')}
              />
              {errors.village && (
                <p className="text-xs text-destructive">{errors.village.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="district" className="after:content-['*'] after:ml-0.5 after:text-destructive">
                District
              </Label>
              <Input
                id="district"
                type="text"
                placeholder="e.g. Salem"
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

            <div className="space-y-2">
              <Label htmlFor="pincode">Pincode</Label>
              <Input
                id="pincode"
                type="text"
                placeholder="e.g. 636001"
                maxLength={10}
                disabled={isLoading}
                {...register('pincode')}
              />
              {errors.pincode && (
                <p className="text-xs text-destructive">{errors.pincode.message}</p>
              )}
            </div>

            <div className="space-y-2 md:col-span-2">
              <Label htmlFor="primaryCrops">Primary Crops Grown</Label>
              <Input
                id="primaryCrops"
                type="text"
                placeholder="e.g. Rice, Wheat, Sugarcane, Cotton"
                maxLength={255}
                disabled={isLoading}
                {...register('primaryCrops')}
              />
              <p className="text-xs text-muted-foreground">Comma-separated list of major crops you cultivate.</p>
              {errors.primaryCrops && (
                <p className="text-xs text-destructive">{errors.primaryCrops.message}</p>
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
