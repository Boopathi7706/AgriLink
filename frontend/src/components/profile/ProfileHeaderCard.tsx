import type { UserResponse } from '@/types/auth';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { User as UserIcon, Mail, Phone, Clock, CheckCircle2 } from 'lucide-react';

interface ProfileHeaderCardProps {
  user: UserResponse;
}

export const ProfileHeaderCard = ({ user }: ProfileHeaderCardProps) => {
  const getRoleBadgeVariant = (role: string) => {
    switch (role) {
      case 'FARMER':
        return 'default';
      case 'BUYER':
        return 'secondary';
      case 'ADMIN':
        return 'destructive';
      default:
        return 'outline';
    }
  };

  const isVerified = user.farmerProfile?.isVerified ?? user.buyerProfile?.isVerified ?? false;

  return (
    <Card className="w-full shadow-sm border-border bg-card">
      <CardContent className="p-6">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center space-x-4">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10 text-primary font-bold text-xl border border-primary/20">
              {user.name ? user.name.charAt(0).toUpperCase() : <UserIcon className="h-8 w-8" />}
            </div>
            <div>
              <div className="flex items-center gap-2 flex-wrap">
                <h2 className="text-xl font-bold text-foreground">{user.name}</h2>
                <Badge variant={getRoleBadgeVariant(user.role)} className="capitalize">
                  {user.role}
                </Badge>
                {user.status && (
                  <Badge variant="outline" className="text-xs">
                    {user.status}
                  </Badge>
                )}
              </div>
              <div className="flex flex-col sm:flex-row sm:items-center gap-2 sm:gap-4 mt-2 text-sm text-muted-foreground">
                <div className="flex items-center gap-1.5">
                  <Mail className="h-4 w-4 text-muted-foreground" />
                  <span>{user.email}</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <Phone className="h-4 w-4 text-muted-foreground" />
                  <span>{user.phoneNumber}</span>
                </div>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2 pt-2 sm:pt-0 border-t sm:border-t-0 w-full sm:w-auto">
            {isVerified ? (
              <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-emerald-50 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-400 text-xs font-semibold border border-emerald-200 dark:border-emerald-800">
                <CheckCircle2 className="h-4 w-4" />
                <span>Verified Account</span>
              </div>
            ) : (
              <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-amber-50 text-amber-700 dark:bg-amber-950/40 dark:text-amber-400 text-xs font-semibold border border-amber-200 dark:border-amber-800">
                <Clock className="h-4 w-4" />
                <span>Pending Verification</span>
              </div>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
