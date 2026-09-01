import * as React from "react"

export const Dialog = ({ children }: { children: React.ReactNode }) => <>{children}</>
export const DialogTrigger = ({ children }: { children: React.ReactNode }) => <>{children}</>
export const DialogContent = ({ children }: { children: React.ReactNode }) => (
  <div className="fixed inset-0 z-50 bg-background/80 backdrop-blur-sm data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0">
    <div className="fixed left-[50%] top-[50%] z-50 grid w-full max-w-lg translate-x-[-50%] translate-y-[-50%] gap-4 border bg-background p-6 shadow-lg duration-200 sm:rounded-lg">
      {children}
    </div>
  </div>
)
export const DialogHeader = ({ children }: { children: React.ReactNode }) => <div className="flex flex-col space-y-1.5 text-center sm:text-left">{children}</div>
export const DialogFooter = ({ children }: { children: React.ReactNode }) => <div className="flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2">{children}</div>
export const DialogTitle = ({ children }: { children: React.ReactNode }) => <h2 className="text-lg font-semibold leading-none tracking-tight">{children}</h2>
export const DialogDescription = ({ children }: { children: React.ReactNode }) => <p className="text-sm text-muted-foreground">{children}</p>
