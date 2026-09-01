import { useAuthStore } from '@/store/useAuthStore';
import { ProfileHeaderCard } from '@/components/profile/ProfileHeaderCard';
import { FarmerProfileForm } from '@/components/profile/FarmerProfileForm';
import { PageContainer } from '@/components/layout/PageContainer';
import { Info } from 'lucide-react';

export const FarmerProfilePage = () => {
  const { user } = useAuthStore();

  if (!user) {
    return null;
  }

  return (
    <PageContainer
      title="Farmer Profile Management"
      description="Manage your farming operational details, location, and crop records."
    >
      <div className="space-y-6 max-w-5xl mx-auto w-full">
        <ProfileHeaderCard user={user} />

        {!user.farmerProfile && (
          <div className="p-4 rounded-lg bg-blue-50 text-blue-800 dark:bg-blue-950/40 dark:text-blue-300 border border-blue-200 dark:border-blue-800 flex items-start gap-3">
            <Info className="h-5 w-5 mt-0.5 shrink-0" />
            <div>
              <p className="font-semibold text-sm">Farmer Profile Notice</p>
              <p className="text-xs mt-0.5">
                No initial farmer profile details were found. Please fill out your location and crop details below to complete your profile.
              </p>
            </div>
          </div>
        )}

        <FarmerProfileForm profile={user.farmerProfile} />
      </div>
    </PageContainer>
  );
};
