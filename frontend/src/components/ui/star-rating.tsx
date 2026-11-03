import { useState } from "react";
import { Star } from "lucide-react";
import { cn } from "../../lib/utils";

const STAR_SIZES = { sm: "size-4", md: "size-5", lg: "size-7" } as const;

interface StarRatingProps {
  value: number | null;
  onChange?: (value: number) => void;
  size?: keyof typeof STAR_SIZES;
  label?: string;
}

export function StarRating({ value, onChange, size = "md", label = "Rating" }: StarRatingProps) {
  const [hover, setHover] = useState<number | null>(null);
  const shown = hover ?? value ?? 0;
  const interactive = Boolean(onChange);

  if (!interactive) {
    return (
      <span className="inline-flex items-center gap-0.5" role="img" aria-label={value ? `${value} out of 5 stars` : "Not rated"}>
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={cn(STAR_SIZES[size], star <= (value ?? 0) ? "fill-accent-400 text-accent-400" : "text-muted-foreground/40")}
          />
        ))}
      </span>
    );
  }

  return (
    <div className="inline-flex items-center gap-1" role="radiogroup" aria-label={label} onMouseLeave={() => setHover(null)}>
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          type="button"
          role="radio"
          aria-checked={value === star}
          aria-label={`${star} star${star > 1 ? "s" : ""}`}
          onMouseEnter={() => setHover(star)}
          onFocus={() => setHover(star)}
          onBlur={() => setHover(null)}
          onClick={() => onChange?.(star)}
          className="rounded-md p-0.5 transition-transform duration-150 hover:scale-110 focus-visible:outline-2 focus-visible:outline-primary-500"
        >
          <Star
            className={cn(
              STAR_SIZES[size],
              "transition-colors duration-150",
              star <= shown ? "fill-accent-400 text-accent-400" : "text-muted-foreground/40",
            )}
          />
        </button>
      ))}
    </div>
  );
}

export function RatingSummary({ average, count }: { average: number | null; count: number }) {
  if (count === 0 || average === null) {
    return <span className="text-sm text-muted-foreground">No ratings yet</span>;
  }
  return (
    <span className="inline-flex items-center gap-2 text-sm">
      <StarRating value={Math.round(average)} size="sm" />
      <span className="font-medium">{average.toFixed(1)}</span>
      <span className="text-muted-foreground">({count})</span>
    </span>
  );
}
