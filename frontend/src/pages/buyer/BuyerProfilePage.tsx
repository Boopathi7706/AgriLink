import { useAuthStore } from '@/store/useAuthStore';
import { ProfileHeaderCard } from '@/components/profile/ProfileHeaderCard';
import { BuyerProfileForm } from '@/components/profile/BuyerProfileForm';
import { PageContainer } from '@/components/layout/PageContainer';
import { Info } from 'lucide-react';

export const BuyerProfilePage = () => {
  const { user } = useAuthStore();

  if (!user) {
    return null;
  }

  return (
    <PageContainer
      title="Buyer Profile Management"
      description="Manage your business information, buyer type, and contact location."
    >
      <div className="space-y-6 max-w-5xl mx-auto w-full">
        <ProfileHeaderCard user={user} />

        {!user.buyerProfile && (
          <div className="p-4 rounded-lg bg-blue-50 text-blue-800 dark:bg-blue-950/40 dark:text-blue-300 border border-blue-200 dark:border-blue-800 flex items-start gap-3">
            <Info className="h-5 w-5 mt-0.5 shrink-0" />
            <div>
              <p className="font-semibold text-sm">Buyer Profile Notice</p>
              <p className="text-xs mt-0.5">
                No initial buyer profile details were found. Please fill out your business name and address details below to complete your profile.
              </p>
            </div>
          </div>
        )}

        <BuyerProfileForm profile={user.buyerProfile} />
      </div>
    </PageContainer>
  );
};
