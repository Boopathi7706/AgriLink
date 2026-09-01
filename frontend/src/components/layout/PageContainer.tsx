import type { ReactNode } from 'react';

interface PageContainerProps {
  children: ReactNode;
  title?: string;
  description?: string;
}

export const PageContainer = ({ children, title, description }: PageContainerProps) => {
  return (
    <div className="flex flex-col space-y-6">
      {(title || description) && (
        <div className="space-y-1.5">
          {title && <h1 className="text-2xl font-semibold tracking-tight">{title}</h1>}
          {description && <p className="text-sm text-muted-foreground">{description}</p>}
        </div>
      )}
      {children}
    </div>
  );
};
