import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { Skeleton } from '@/components/ui/skeleton';

export const PublicOnlyRoute = () => {
  const { isAuthenticated, isHydrating, user } = useAuthStore();

  if (isHydrating) {
    return (
      <div className="flex h-screen w-full items-center justify-center p-8">
        <div className="flex flex-col items-center space-y-4 max-w-sm w-full">
          <Skeleton className="h-12 w-12 rounded-full" />
          <Skeleton className="h-4 w-48" />
        </div>
      </div>
    );
  }

  if (isAuthenticated && user) {
    switch (user.role) {
      case 'FARMER':
        return <Navigate to="/farmer" replace />;
      case 'BUYER':
        return <Navigate to="/buyer" replace />;
      case 'ADMIN':
        return <Navigate to="/admin" replace />;
      default:
        return <Navigate to="/" replace />;
    }
  }

  return <Outlet />;
};
