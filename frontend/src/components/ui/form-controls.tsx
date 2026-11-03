import { forwardRef, type InputHTMLAttributes, type ReactNode, type SelectHTMLAttributes, type TextareaHTMLAttributes } from "react";
import { cn } from "../../lib/utils";

interface FieldProps {
  label: string;
  htmlFor: string;
  error?: string;
  hint?: string;
  children: ReactNode;
}

export function Field({ label, htmlFor, error, hint, children }: FieldProps) {
  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={htmlFor} className="text-sm font-medium text-foreground">
        {label}
      </label>
      {children}
      {error ? (
        <p role="alert" className="text-sm text-error-600 dark:text-error-500">
          {error}
        </p>
      ) : (
        hint && <p className="text-xs text-muted-foreground">{hint}</p>
      )}
    </div>
  );
}

const controlBase =
  "w-full rounded-xl border bg-card px-3.5 text-sm text-foreground placeholder:text-muted-foreground/80 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-primary-500/40 focus:border-primary-500 disabled:opacity-60";

function controlClass(invalid: boolean) {
  return cn(controlBase, invalid ? "border-error-500" : "border-input");
}

export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement> & { invalid?: boolean }>(
  function Input({ invalid = false, className, ...props }, ref) {
    return <input ref={ref} className={cn(controlClass(invalid), "h-11", className)} {...props} />;
  },
);

export const Select = forwardRef<HTMLSelectElement, SelectHTMLAttributes<HTMLSelectElement> & { invalid?: boolean }>(
  function Select({ invalid = false, className, children, ...props }, ref) {
    return (
      <select ref={ref} className={cn(controlClass(invalid), "h-11 pr-8", className)} {...props}>
        {children}
      </select>
    );
  },
);

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaHTMLAttributes<HTMLTextAreaElement> & { invalid?: boolean }>(
  function Textarea({ invalid = false, className, ...props }, ref) {
    return <textarea ref={ref} className={cn(controlClass(invalid), "min-h-28 py-3", className)} {...props} />;
  },
);
