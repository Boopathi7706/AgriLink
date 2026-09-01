import * as React from "react"

export const DropdownMenu = ({ children }: { children: React.ReactNode }) => <>{children}</>
export const DropdownMenuTrigger = ({ children }: { children: React.ReactNode }) => <>{children}</>
export const DropdownMenuContent = ({ children }: { children: React.ReactNode }) => (
  <div className="z-50 min-w-[8rem] overflow-hidden rounded-md border bg-popover p-1 text-popover-foreground shadow-md data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2">
    {children}
  </div>
)
export const DropdownMenuItem = ({ children }: { children: React.ReactNode }) => (
  <div className="relative flex cursor-default select-none items-center rounded-sm px-2 py-1.5 text-sm outline-none transition-colors focus:bg-accent focus:text-accent-foreground data-[disabled]:pointer-events-none data-[disabled]:opacity-50">
    {children}
  </div>
)
export const DropdownMenuLabel = ({ children }: { children: React.ReactNode }) => <div className="px-2 py-1.5 text-sm font-semibold">{children}</div>
export const DropdownMenuSeparator = () => <div className="-mx-1 my-1 h-px bg-muted" />
