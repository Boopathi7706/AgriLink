import { Link } from 'react-router-dom';
import { Sprout } from 'lucide-react';

export const Header = () => {
  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-14 items-center px-4 md:px-6 max-w-7xl mx-auto">
        <Link to="/" className="flex items-center gap-2">
          <Sprout className="h-6 w-6 text-primary" />
          <span className="font-bold sm:inline-block">AgriLink</span>
        </Link>
        <div className="flex flex-1 items-center justify-end space-x-4">
          <nav className="flex items-center space-x-4">
            <Link to="/farmer" className="text-sm font-medium hover:text-primary">
              Farmer
            </Link>
            <Link to="/buyer" className="text-sm font-medium hover:text-primary">
              Buyer
            </Link>
            <Link to="/admin" className="text-sm font-medium hover:text-primary text-muted-foreground">
              Admin
            </Link>
          </nav>
        </div>
      </div>
    </header>
  );
};
