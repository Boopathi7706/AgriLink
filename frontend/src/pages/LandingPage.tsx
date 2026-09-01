import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { MainContent } from '@/components/layout/MainContent';
import { Sprout, TrendingUp, ShieldCheck } from 'lucide-react';

export const LandingPage = () => {
  return (
    <MainContent>
      <div className="flex flex-col items-center justify-center space-y-12 py-12 md:py-24 lg:py-32">
        <div className="container px-4 md:px-6 flex flex-col items-center text-center space-y-6">
          <div className="space-y-4 max-w-3xl">
            <h1 className="text-4xl font-bold tracking-tighter sm:text-5xl md:text-6xl lg:text-7xl">
              Smart Market Linkages for <span className="text-primary">Farmers</span>
            </h1>
            <p className="mx-auto max-w-[700px] text-muted-foreground md:text-xl">
              AgriLink directly connects farmers and buyers while providing transparent market prices and logistics tracking.
            </p>
          </div>
          <div className="flex flex-col sm:flex-row gap-4">
            <Button size="lg" asChild>
              <Link to="/farmer">Get Started</Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link to="/buyer">Explore Market Prices</Link>
            </Button>
          </div>
        </div>
        
        <div className="container px-4 md:px-6 mt-16 max-w-6xl mx-auto">
          <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
            <Card>
              <CardHeader className="flex flex-row items-center gap-4 space-y-0">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                  <Sprout className="h-6 w-6 text-primary" />
                </div>
                <div className="space-y-1">
                  <CardTitle>Direct Marketplace</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <CardDescription className="text-base">
                  Bypass intermediaries and sell your produce directly to verified buyers for better margins.
                </CardDescription>
              </CardContent>
            </Card>
            
            <Card>
              <CardHeader className="flex flex-row items-center gap-4 space-y-0">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                  <TrendingUp className="h-6 w-6 text-primary" />
                </div>
                <div className="space-y-1">
                  <CardTitle>Price Intelligence</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <CardDescription className="text-base">
                  Access real-time Mandi prices to make informed decisions about when and where to sell.
                </CardDescription>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center gap-4 space-y-0">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                  <ShieldCheck className="h-6 w-6 text-primary" />
                </div>
                <div className="space-y-1">
                  <CardTitle>Trusted Network</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <CardDescription className="text-base">
                  Transact safely with verified users, built-in ratings, and reliable transport tracking.
                </CardDescription>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </MainContent>
  );
};
