import { Link } from "react-router-dom";
import { ArrowRight, GraduationCap } from "lucide-react";
import { Button } from "../ui/button";
import { ThemeToggle } from "../layout/ThemeToggle";

export function LandingHeader() {
  return (
    <header className="sticky top-0 z-40 border-b border-border bg-background/85 backdrop-blur-md">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between gap-3 px-4 sm:px-6">
        <Link to="/" className="flex items-center gap-2 font-semibold tracking-tight">
          <span className="flex size-8 items-center justify-center rounded-lg bg-primary-600 text-white dark:bg-primary-500 dark:text-slate-950">
            <GraduationCap className="size-4" />
          </span>
          CampusSkill
        </Link>
        <div className="flex items-center gap-2">
          <ThemeToggle />
          <Link to="/login" className="hidden sm:block">
            <Button variant="ghost" size="sm">
              Log in
            </Button>
          </Link>
          <Link to="/signup">
            <Button size="sm">Sign up</Button>
          </Link>
        </div>
      </div>
    </header>
  );
}

export function Hero({ imageUrl }: { imageUrl: string }) {
  return (
    <section className="relative overflow-hidden">
      <div className="pointer-events-none absolute inset-0 -z-10 bg-gradient-to-br from-primary-100 via-secondary-50 to-background dark:from-primary-900/30 dark:via-secondary-900/10" />
      <div className="mx-auto grid max-w-7xl items-center gap-12 px-4 py-16 sm:px-6 sm:py-20 lg:grid-cols-2 lg:py-28">
        <div className="animate-fade-up">
          <span className="inline-flex items-center gap-2 rounded-full border border-primary-200 bg-card px-3 py-1 text-sm font-medium text-primary-800 dark:border-primary-800 dark:text-primary-200">
            Peer learning for your campus
          </span>
          <h1 className="mt-6 text-4xl font-bold leading-[1.1] sm:text-5xl lg:text-6xl">
            Learn Together.
            <br />
            <span className="bg-gradient-to-r from-primary-600 to-secondary-600 bg-clip-text text-transparent dark:from-primary-300 dark:to-secondary-300">Grow Together.</span>
          </h1>
          <p className="mt-6 max-w-xl text-lg leading-relaxed text-muted-foreground">
            Trade what you know for what you need. Teach a skill you are great at, learn one you want, and
            form study groups with students who share your goals.
          </p>
          <div className="mt-10 flex flex-col gap-3 sm:flex-row">
            <Link to="/signup">
              <Button size="lg" className="w-full sm:w-auto">
                Get started
                <ArrowRight className="size-4" />
              </Button>
            </Link>
            <a href="#skills">
              <Button size="lg" variant="secondary" className="w-full sm:w-auto">
                Explore Skills
              </Button>
            </a>
          </div>
        </div>

        <div className="relative animate-fade-up [animation-delay:150ms]">
          <div className="overflow-hidden rounded-3xl border border-border bg-card shadow-2xl">
            <img
              src={imageUrl}
              alt="Students studying together outdoors on campus"
              className="aspect-[4/3] w-full object-cover"
              loading="eager"
            />
          </div>
          <div className="absolute -bottom-6 -left-4 hidden rounded-2xl border border-border bg-card/95 px-5 py-4 shadow-xl backdrop-blur sm:block">
            <p className="text-sm font-semibold">Skill swap accepted</p>
            <p className="text-xs text-muted-foreground">Calculus help for UI design sessions</p>
          </div>
        </div>
      </div>
    </section>
  );
}

export const HOW_IT_WORKS = [
  {
    title: "Build your profile",
    body: "List the skills you can teach and the ones you want to learn, then add your branch and year.",
  },
  {
    title: "Send a swap request",
    body: "Find a tutor for a skill, pick what you want to learn, and send a request in a few clicks.",
  },
  {
    title: "Complete and rate",
    body: "Finish the exchange together, then rate the experience so great tutors stand out.",
  },
];
