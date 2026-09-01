import { createBrowserRouter } from 'react-router-dom';
import { AppShell } from './components/layout/AppShell';
import { LandingPage } from './pages/LandingPage';
import { LoginPage } from './pages/auth/LoginPage';
import { RegisterPage } from './pages/auth/RegisterPage';
import { PlaceholderPage } from './pages/PlaceholderPage';
import { NotFoundPage } from './pages/NotFoundPage';
import { UnauthorizedPage } from './pages/UnauthorizedPage';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { RoleProtectedRoute } from './components/auth/RoleProtectedRoute';
import { PublicOnlyRoute } from './components/auth/PublicOnlyRoute';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppShell />,
    errorElement: <NotFoundPage />,
    children: [
      {
        index: true,
        element: <LandingPage />,
      },
      {
        element: <PublicOnlyRoute />,
        children: [
          {
            path: 'login',
            element: <LoginPage />,
          },
          {
            path: 'register',
            element: <RegisterPage />,
          },
        ],
      },
      {
        path: 'unauthorized',
        element: <UnauthorizedPage />,
      },
      {
        element: <ProtectedRoute />,
        children: [
          {
            element: <RoleProtectedRoute allowedRoles={['FARMER']} />,
            children: [
              {
                path: 'farmer',
                element: <PlaceholderPage title="Farmer Area" description="This section will be implemented in a future feature." />,
              },
            ],
          },
          {
            element: <RoleProtectedRoute allowedRoles={['BUYER']} />,
            children: [
              {
                path: 'buyer',
                element: <PlaceholderPage title="Buyer Area" description="This section will be implemented in a future feature." />,
              },
            ],
          },
          {
            element: <RoleProtectedRoute allowedRoles={['ADMIN']} />,
            children: [
              {
                path: 'admin',
                element: <PlaceholderPage title="Admin Area" description="This section will be implemented in a future feature." />,
              },
            ],
          },
        ],
      },
    ],
  },
]);
