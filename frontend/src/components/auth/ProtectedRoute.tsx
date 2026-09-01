import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { Skeleton } from '@/components/ui/skeleton';

export const ProtectedRoute = () => {
  const { isAuthenticated, isHydrating } = useAuthStore();

  if (isHydrating) {
    return (
      <div className="flex h-screen w-full items-center justify-center p-8">
        <div className="flex flex-col items-center space-y-4 max-w-sm w-full">
          <Skeleton className="h-12 w-12 rounded-full" />
          <Skeleton className="h-4 w-48" />
          <Skeleton className="h-4 w-32" />
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};
