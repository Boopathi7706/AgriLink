import { PageContainer } from '@/components/layout/PageContainer';
import { MainContent } from '@/components/layout/MainContent';
import { Sidebar } from '@/components/layout/Sidebar';

interface PlaceholderPageProps {
  title: string;
  description: string;
}

export const PlaceholderPage = ({ title, description }: PlaceholderPageProps) => {
  return (
    <div className="flex flex-1">
      <Sidebar />
      <MainContent>
        <PageContainer title={title} description={description}>
          <div className="flex h-[450px] shrink-0 items-center justify-center rounded-md border border-dashed">
            <div className="mx-auto flex max-w-[420px] flex-col items-center justify-center text-center">
              <h3 className="mt-4 text-lg font-semibold">{title}</h3>
              <p className="mb-4 mt-2 text-sm text-muted-foreground">
                {description}
              </p>
            </div>
          </div>
        </PageContainer>
      </MainContent>
    </div>
  );
};
