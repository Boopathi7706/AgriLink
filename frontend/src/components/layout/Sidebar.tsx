export const Sidebar = () => {
  return (
    <aside className="w-64 border-r bg-muted/40 hidden md:block min-h-[calc(100vh-3.5rem)]">
      <div className="p-4">
        <nav className="space-y-2">
          <div className="text-sm font-medium text-muted-foreground px-2">Navigation</div>
          {/* Future navigation links go here */}
        </nav>
      </div>
    </aside>
  );
};
