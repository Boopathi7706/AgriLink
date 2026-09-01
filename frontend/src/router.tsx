import { createBrowserRouter } from 'react-router-dom';
import { AppShell } from './components/layout/AppShell';
import { LandingPage } from './pages/LandingPage';
import { PlaceholderPage } from './pages/PlaceholderPage';
import { NotFoundPage } from './pages/NotFoundPage';

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
        path: 'farmer',
        element: <PlaceholderPage title="Farmer Area" description="This section will be implemented in a future feature." />,
      },
      {
        path: 'buyer',
        element: <PlaceholderPage title="Buyer Area" description="This section will be implemented in a future feature." />,
      },
      {
        path: 'admin',
        element: <PlaceholderPage title="Admin Area" description="This section will be implemented in a future feature." />,
      },
    ],
  },
]);
