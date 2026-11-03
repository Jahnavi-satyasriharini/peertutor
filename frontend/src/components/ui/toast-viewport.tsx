import { CheckCircle2, Info, X, XCircle } from "lucide-react";
import { useToast } from "../../hooks/useToast";
import { cn } from "../../lib/utils";
import type { ToastTone } from "../../context/ToastContext";

const toneStyles: Record<ToastTone, string> = {
  success: "border-success-200 dark:border-success-800",
  error: "border-error-200 dark:border-error-800",
  info: "border-secondary-200 dark:border-secondary-800",
};

const toneIcons: Record<ToastTone, typeof Info> = {
  success: CheckCircle2,
  error: XCircle,
  info: Info,
};

const toneIconColors: Record<ToastTone, string> = {
  success: "text-success-600 dark:text-success-500",
  error: "text-error-600 dark:text-error-500",
  info: "text-secondary-600 dark:text-secondary-400",
};

export function ToastViewport() {
  const { toasts, dismiss } = useToast();

  return (
    <div
      aria-live="polite"
      className="pointer-events-none fixed inset-x-4 bottom-4 z-[60] flex flex-col items-end gap-3 sm:inset-x-auto sm:right-6 sm:bottom-6"
    >
      {toasts.map((toast) => {
        const Icon = toneIcons[toast.tone];
        return (
          <div
            key={toast.id}
            role={toast.tone === "error" ? "alert" : "status"}
            className={cn(
              "pointer-events-auto flex w-full max-w-sm items-start gap-3 rounded-xl border bg-card p-4 shadow-xl animate-fade-up",
              toneStyles[toast.tone],
            )}
          >
            <Icon className={cn("mt-0.5 size-5 shrink-0", toneIconColors[toast.tone])} />
            <div className="min-w-0 flex-1">
              <p className="text-sm font-semibold">{toast.title}</p>
              {toast.description && <p className="mt-0.5 text-sm text-muted-foreground">{toast.description}</p>}
            </div>
            <button
              type="button"
              onClick={() => dismiss(toast.id)}
              aria-label="Dismiss notification"
              className="rounded-md p-1 text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
            >
              <X className="size-4" />
            </button>
          </div>
        );
      })}
    </div>
  );
}
