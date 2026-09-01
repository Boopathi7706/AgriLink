import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link, useNavigate } from 'react-router-dom';
import { registerApi } from '@/api/auth';
import { useAuthStore } from '@/store/useAuthStore';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Sprout, Eye, EyeOff, Loader2, Tractor, Store } from 'lucide-react';
import type { BuyerType, ErrorResponse, RegisterRequest } from '@/types/auth';
import axios from 'axios';

const registerSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters').max(120, 'Name must not exceed 120 characters'),
  email: z.string().min(1, 'Email is required').email('Please enter a valid email address').max(150),
  password: z.string().min(8, 'Password must be at least 8 characters').max(64),
  phoneNumber: z.string().regex(/^[0-9]{10}$/, 'Phone number must be a valid 10-digit number'),
  role: z.enum(['FARMER', 'BUYER']),
  
  // Farmer fields
  district: z.string().min(1, 'District is required').max(100),
  state: z.string().min(1, 'State is required').max(100),
  farmSizeAcres: z.string().optional(),
  village: z.string().max(100).optional(),
  pincode: z.string().max(10).optional(),
  primaryCrops: z.string().max(255).optional(),

  // Buyer fields
  businessName: z.string().optional(),
  buyerType: z.enum(['WHOLESALER', 'RETAILER', 'PROCESSOR', 'EXPORTER', 'INDIVIDUAL']).optional(),
  gstin: z.string().max(20).optional(),
  address: z.string().max(255).optional(),
}).superRefine((data, ctx) => {
  if (data.role === 'BUYER') {
    if (!data.businessName || data.businessName.trim() === '') {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['businessName'],
        message: 'Business name is required for Buyer account',
      });
    }
    if (!data.buyerType) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['buyerType'],
        message: 'Buyer type is required',
      });
    }
  }
});

type RegisterFormValues = z.infer<typeof registerSchema>;

export const RegisterPage = () => {
  const [selectedRole, setSelectedRole] = useState<'FARMER' | 'BUYER'>('FARMER');
  const [showPassword, setShowPassword] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);
  const [validationErrors, setValidationErrors] = useState<Record<string, string> | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      role: 'FARMER',
      buyerType: 'WHOLESALER',
    },
  });

  const handleRoleSelect = (role: 'FARMER' | 'BUYER') => {
    setSelectedRole(role);
    setValue('role', role);
  };

  const onSubmit = async (data: RegisterFormValues) => {
    setIsLoading(true);
    setServerError(null);
    setValidationErrors(null);

    const payload: RegisterRequest = {
      name: data.name,
      email: data.email,
      password: data.password,
      phoneNumber: data.phoneNumber,
      role: data.role,
    };

    if (data.role === 'FARMER') {
      const parsedAcres = data.farmSizeAcres && data.farmSizeAcres.trim() !== '' ? Number(data.farmSizeAcres) : undefined;
      payload.farmerProfile = {
        district: data.district,
        state: data.state,
        farmSizeAcres: parsedAcres && !isNaN(parsedAcres) ? parsedAcres : undefined,
        village: data.village || undefined,
        pincode: data.pincode || undefined,
        primaryCrops: data.primaryCrops || undefined,
      };
    } else {
      payload.buyerProfile = {
        businessName: data.businessName || '',
        buyerType: (data.buyerType || 'WHOLESALER') as BuyerType,
        district: data.district,
        state: data.state,
        gstin: data.gstin || undefined,
        address: data.address || undefined,
        pincode: data.pincode || undefined,
      };
    }

    try {
      const response = await registerApi(payload);
      setAuth(response.accessToken, response.user);

      if (response.user.role === 'FARMER') {
        navigate('/farmer', { replace: true });
      } else {
        navigate('/buyer', { replace: true });
      }
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response?.data) {
        const errorData = err.response.data as ErrorResponse;
        setServerError(errorData.message || 'Registration failed. Please check your information.');
        if (errorData.validationErrors) {
          setValidationErrors(errorData.validationErrors);
        }
      } else {
        setServerError('Network error. Unable to connect to server.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-[calc(100vh-3.5rem)] items-center justify-center p-4 bg-muted/20 my-8">
      <Card className="w-full max-w-xl shadow-lg border-border">
        <CardHeader className="space-y-2 text-center">
          <div className="flex justify-center mb-2">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 text-primary">
              <Sprout className="h-6 w-6" />
            </div>
          </div>
          <CardTitle className="text-2xl font-bold">Join AgriLink</CardTitle>
          <CardDescription>
            Create an account to connect directly with agricultural markets
          </CardDescription>
        </CardHeader>

        <form onSubmit={handleSubmit(onSubmit)}>
          <CardContent className="space-y-6">
            {serverError && (
              <div className="p-3 text-sm rounded-md bg-destructive/10 text-destructive border border-destructive/20 font-medium">
                {serverError}
                {validationErrors && (
                  <ul className="list-disc list-inside mt-2 space-y-1 text-xs">
                    {Object.entries(validationErrors).map(([field, msg]) => (
                      <li key={field}>
                        <strong className="capitalize">{field}:</strong> {msg}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            )}

            {/* Role Selection Toggle */}
            <div className="space-y-2">
              <Label className="text-sm font-medium">Select Account Type</Label>
              <div className="grid grid-cols-2 gap-4">
                <button
                  type="button"
                  onClick={() => handleRoleSelect('FARMER')}
                  className={`flex flex-col items-center justify-center p-4 rounded-lg border-2 transition-all ${
                    selectedRole === 'FARMER'
                      ? 'border-primary bg-primary/5 text-primary font-semibold'
                      : 'border-border bg-background hover:bg-muted text-muted-foreground'
                  }`}
                >
                  <Tractor className="h-8 w-8 mb-2" />
                  <span>I am a Farmer</span>
                </button>

                <button
                  type="button"
                  onClick={() => handleRoleSelect('BUYER')}
                  className={`flex flex-col items-center justify-center p-4 rounded-lg border-2 transition-all ${
                    selectedRole === 'BUYER'
                      ? 'border-primary bg-primary/5 text-primary font-semibold'
                      : 'border-border bg-background hover:bg-muted text-muted-foreground'
                  }`}
                >
                  <Store className="h-8 w-8 mb-2" />
                  <span>I am a Buyer</span>
                </button>
              </div>
            </div>

            {/* Personal Details */}
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider">
                Personal Information
              </h3>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Full Name *</Label>
                  <Input id="name" placeholder="John Doe" disabled={isLoading} {...register('name')} />
                  {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="phoneNumber">Phone Number (10 Digits) *</Label>
                  <Input id="phoneNumber" placeholder="9876543210" disabled={isLoading} {...register('phoneNumber')} />
                  {errors.phoneNumber && <p className="text-xs text-destructive">{errors.phoneNumber.message}</p>}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="email">Email Address *</Label>
                  <Input id="email" type="email" placeholder="john@example.com" disabled={isLoading} {...register('email')} />
                  {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="password">Password (Min 8 chars) *</Label>
                  <div className="relative">
                    <Input
                      id="password"
                      type={showPassword ? 'text' : 'password'}
                      placeholder="••••••••"
                      disabled={isLoading}
                      {...register('password')}
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      className="absolute right-0 top-0 h-full px-3 py-2 text-muted-foreground hover:bg-transparent"
                      onClick={() => setShowPassword(!showPassword)}
                      tabIndex={-1}
                    >
                      {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                    </Button>
                  </div>
                  {errors.password && <p className="text-xs text-destructive">{errors.password.message}</p>}
                </div>
              </div>
            </div>

            {/* Location & Profile Details */}
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider">
                {selectedRole === 'FARMER' ? 'Farmer Profile & Location' : 'Buyer Business & Location'}
              </h3>

              {selectedRole === 'BUYER' && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="businessName">Business Name *</Label>
                    <Input id="businessName" placeholder="Green Harvest Traders" disabled={isLoading} {...register('businessName')} />
                    {errors.businessName && <p className="text-xs text-destructive">{errors.businessName.message}</p>}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="buyerType">Buyer Type *</Label>
                    <select
                      id="buyerType"
                      className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
                      disabled={isLoading}
                      {...register('buyerType')}
                    >
                      <option value="WHOLESALER">Wholesaler</option>
                      <option value="RETAILER">Retailer</option>
                      <option value="PROCESSOR">Processor</option>
                      <option value="EXPORTER">Exporter</option>
                      <option value="INDIVIDUAL">Individual</option>
                    </select>
                    {errors.buyerType && <p className="text-xs text-destructive">{errors.buyerType.message}</p>}
                  </div>
                </div>
              )}

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="district">District *</Label>
                  <Input id="district" placeholder="Coimbatore" disabled={isLoading} {...register('district')} />
                  {errors.district && <p className="text-xs text-destructive">{errors.district.message}</p>}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="state">State *</Label>
                  <Input id="state" placeholder="Tamil Nadu" disabled={isLoading} {...register('state')} />
                  {errors.state && <p className="text-xs text-destructive">{errors.state.message}</p>}
                </div>
              </div>

              {selectedRole === 'FARMER' ? (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="farmSizeAcres">Farm Size (Acres)</Label>
                    <Input id="farmSizeAcres" type="number" step="0.1" placeholder="5.5" disabled={isLoading} {...register('farmSizeAcres')} />
                    {errors.farmSizeAcres && <p className="text-xs text-destructive">{errors.farmSizeAcres.message}</p>}
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="primaryCrops">Primary Crops</Label>
                    <Input id="primaryCrops" placeholder="Rice, Wheat, Cotton" disabled={isLoading} {...register('primaryCrops')} />
                  </div>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="gstin">GSTIN (Optional)</Label>
                    <Input id="gstin" placeholder="33AAAAA0000A1Z5" disabled={isLoading} {...register('gstin')} />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="address">Address</Label>
                    <Input id="address" placeholder="123 Market Road" disabled={isLoading} {...register('address')} />
                  </div>
                </div>
              )}
            </div>
          </CardContent>

          <CardFooter className="flex flex-col space-y-4">
            <Button type="submit" className="w-full" size="lg" disabled={isLoading}>
              {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              {isLoading ? 'Creating Account...' : 'Create Account'}
            </Button>

            <div className="text-center text-sm text-muted-foreground">
              Already have an account?{' '}
              <Link to="/login" className="font-semibold text-primary hover:underline">
                Sign in
              </Link>
            </div>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};
