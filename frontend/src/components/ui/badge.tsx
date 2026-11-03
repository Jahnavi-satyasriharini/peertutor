import type { ReactNode } from "react";
import { cn } from "../../lib/utils";

export type BadgeTone = "neutral" | "primary" | "secondary" | "accent" | "success" | "warning" | "error";

const tones: Record<BadgeTone, string> = {
  neutral: "bg-muted text-foreground border-border",
  primary: "bg-primary-100 text-primary-800 border-primary-200 dark:bg-primary-900/50 dark:text-primary-100 dark:border-primary-800",
  secondary: "bg-secondary-100 text-secondary-800 border-secondary-200 dark:bg-secondary-900/50 dark:text-secondary-100 dark:border-secondary-800",
  accent: "bg-accent-100 text-accent-800 border-accent-200 dark:bg-accent-900/50 dark:text-accent-100 dark:border-accent-800",
  success: "bg-success-100 text-success-800 border-success-200 dark:bg-success-900/50 dark:text-success-100 dark:border-success-800",
  warning: "bg-warning-100 text-warning-800 border-warning-200 dark:bg-warning-900/50 dark:text-warning-100 dark:border-warning-800",
  error: "bg-error-100 text-error-800 border-error-200 dark:bg-error-900/50 dark:text-error-100 dark:border-error-800",
};

export function Badge({ tone = "neutral", children, className }: { tone?: BadgeTone; children: ReactNode; className?: string }) {
  return (
    <span
      className={cn(
        "inline-flex items-center gap-1 rounded-full border px-2.5 py-0.5 text-xs font-medium whitespace-nowrap",
        tones[tone],
        className,
      )}
    >
      {children}
    </span>
  );
}
