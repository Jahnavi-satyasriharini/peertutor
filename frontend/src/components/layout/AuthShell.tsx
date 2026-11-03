import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import { GraduationCap } from "lucide-react";
import { ThemeToggle } from "./ThemeToggle";

export function AuthShell({ title, subtitle, children }: { title: string; subtitle: string; children: ReactNode }) {
  return (
    <div className="relative flex min-h-dvh flex-col bg-background">
      <div className="absolute right-4 top-4 sm:right-6 sm:top-6">
        <ThemeToggle />
      </div>
      <div className="flex flex-1 items-center justify-center px-4 py-16">
        <div className="w-full max-w-md animate-fade-up">
          <Link to="/" className="mb-8 flex items-center justify-center gap-2 font-semibold">
            <span className="flex size-10 items-center justify-center rounded-xl bg-primary-600 text-white dark:bg-primary-500 dark:text-slate-950">
              <GraduationCap className="size-5" />
            </span>
            <span className="text-lg">CampusSkill</span>
          </Link>
          <div className="rounded-2xl border border-border bg-card p-6 shadow-lg sm:p-8">
            <h1 className="text-2xl font-bold">{title}</h1>
            <p className="mt-2 text-sm text-muted-foreground">{subtitle}</p>
            <div className="mt-6">{children}</div>
          </div>
        </div>
      </div>
    </div>
  );
}
