import { Outlet } from 'react-router-dom';
import { Header } from './Header';

export const AppShell = () => {
  return (
    <div className="relative flex min-h-screen flex-col bg-background">
      <Header />
      <div className="flex-1 flex flex-col">
        <Outlet />
      </div>
    </div>
  );
};
