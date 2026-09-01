import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';

export const NotFoundPage = () => {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-background text-center">
      <h1 className="text-4xl font-bold tracking-tight mb-4">404 - Not Found</h1>
      <p className="text-muted-foreground mb-8">The page you are looking for does not exist.</p>
      <Button asChild>
        <Link to="/">Return to Home</Link>
      </Button>
    </div>
  );
};
