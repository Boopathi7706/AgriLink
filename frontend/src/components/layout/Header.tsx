import { Link, useNavigate } from 'react-router-dom';
import { Sprout, LogOut, User as UserIcon } from 'lucide-react';
import { useAuthStore } from '@/store/useAuthStore';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';

export const Header = () => {
  const { user, isAuthenticated, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

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

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-14 items-center px-4 md:px-6 max-w-7xl mx-auto">
        <Link to="/" className="flex items-center gap-2 mr-6">
          <Sprout className="h-6 w-6 text-primary" />
          <span className="font-bold sm:inline-block">AgriLink</span>
        </Link>

        <div className="flex flex-1 items-center justify-end space-x-4">
          {isAuthenticated && user ? (
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2 text-sm font-medium">
                <UserIcon className="h-4 w-4 text-muted-foreground" />
                <span className="hidden sm:inline-block">{user.name}</span>
                <Badge variant={getRoleBadgeVariant(user.role)} className="text-xs">
                  {user.role}
                </Badge>
              </div>

              <nav className="flex items-center space-x-2">
                {user.role === 'FARMER' && (
                  <Link to="/farmer" className="text-sm font-medium hover:text-primary px-2">
                    Farmer Area
                  </Link>
                )}
                {user.role === 'BUYER' && (
                  <Link to="/buyer" className="text-sm font-medium hover:text-primary px-2">
                    Buyer Area
                  </Link>
                )}
                {user.role === 'ADMIN' && (
                  <Link to="/admin" className="text-sm font-medium hover:text-primary px-2">
                    Admin Area
                  </Link>
                )}
              </nav>

              <Button variant="outline" size="sm" onClick={handleLogout} className="flex items-center gap-1.5">
                <LogOut className="h-3.5 w-3.5" />
                <span>Logout</span>
              </Button>
            </div>
          ) : (
            <nav className="flex items-center space-x-3">
              <Button variant="ghost" size="sm" asChild>
                <Link to="/login">Sign In</Link>
              </Button>
              <Button size="sm" asChild>
                <Link to="/register">Get Started</Link>
              </Button>
            </nav>
          )}
        </div>
      </div>
    </header>
  );
};
