import { Link } from "react-router-dom";
import { ArrowRight, BarChart3, Repeat, Search, ShieldCheck, Star, Users } from "lucide-react";
import { Badge } from "../ui/badge";
import { Button } from "../ui/button";
import { Card } from "../ui/card";
import { EmptyState } from "../ui/feedback";
import { RatingSummary } from "../ui/star-rating";
import { initials } from "../../lib/format";
import type { PreviewGroup, PreviewTutor, Skill } from "../../types/api";
import { HOW_IT_WORKS } from "./LandingHero";

const FEATURES = [
  { icon: Search, title: "Find peer tutors", body: "Search classmates by skill, branch and year to find the right person to learn from." },
  { icon: Repeat, title: "Swap skills", body: "Send a request, get it accepted, and trade what you know for what you need." },
  { icon: Users, title: "Study groups", body: "Create or join online and in-person groups with students who share your goals." },
  { icon: Star, title: "Ratings that matter", body: "Rate completed exchanges so the most helpful tutors rise to the top." },
  { icon: BarChart3, title: "Personal dashboard", body: "Track pending requests, completed swaps and your groups at a glance." },
  { icon: ShieldCheck, title: "Campus only", body: "Sign-up is limited to your college email, so everyone is a real classmate." },
];

export function FeatureGrid() {
  return (
    <section className="mx-auto max-w-7xl px-4 py-16 sm:px-6">
      <div className="mx-auto max-w-2xl text-center">
        <h2 className="text-3xl font-bold sm:text-4xl">Everything you need to learn from each other</h2>
        <p className="mt-3 text-muted-foreground">Built for students, by the way students actually study.</p>
      </div>
      <div className="mt-12 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {FEATURES.map((f) => (
          <Card key={f.title} className="p-6 hover:-translate-y-1 hover:shadow-lg">
            <span className="flex size-11 items-center justify-center rounded-xl bg-gradient-to-br from-primary-500 to-secondary-600 text-white">
              <f.icon className="size-5" />
            </span>
            <h3 className="mt-5 text-lg font-semibold">{f.title}</h3>
            <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{f.body}</p>
          </Card>
        ))}
      </div>
    </section>
  );
}

export function HowItWorks() {
  return (
    <section className="mx-auto max-w-7xl px-4 py-16 sm:px-6">
      <div className="mx-auto max-w-2xl text-center">
        <h2 className="text-3xl font-bold sm:text-4xl">How CampusSkill works</h2>
        <p className="mt-3 text-muted-foreground">A simple loop that turns your existing knowledge into new skills.</p>
      </div>
      <div className="mt-12 grid gap-6 md:grid-cols-3">
        {HOW_IT_WORKS.map((step, index) => (
          <Card key={step.title} className="p-6 transition-transform duration-300 hover:-translate-y-1 hover:shadow-lg">
            <span className="flex size-10 items-center justify-center rounded-xl bg-primary-100 font-semibold text-primary-800 dark:bg-primary-900 dark:text-primary-100">
              {index + 1}
            </span>
            <h3 className="mt-5 text-lg font-semibold">{step.title}</h3>
            <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{step.body}</p>
          </Card>
        ))}
      </div>
    </section>
  );
}

export function TutorPreviewGrid({ tutors }: { tutors: PreviewTutor[] }) {
  if (tutors.length === 0) {
    return <EmptyState title="No tutors yet" description="Be the first to share a skill on your profile." />;
  }
  return (
    <div className="grid gap-5 md:grid-cols-3">
      {tutors.map((tutor) => (
        <Card key={tutor.name} className="p-6 hover:-translate-y-1 hover:shadow-lg">
          <div className="flex items-center gap-3">
            <span className="flex size-11 items-center justify-center rounded-full bg-secondary-100 font-semibold text-secondary-800 dark:bg-secondary-900 dark:text-secondary-100">
              {initials(tutor.name)}
            </span>
            <div className="min-w-0">
              <p className="truncate font-semibold">{tutor.name}</p>
              <p className="truncate text-sm text-muted-foreground">{tutor.branch ?? "Campus student"}</p>
            </div>
          </div>
          <div className="mt-4">
            <RatingSummary average={tutor.averageRating} count={tutor.ratingCount} />
          </div>
          <div className="mt-4 flex flex-wrap gap-2">
            {tutor.teachSkills.map((skill) => (
              <Badge key={skill.id} tone="primary">
                {skill.name}
              </Badge>
            ))}
          </div>
        </Card>
      ))}
    </div>
  );
}

export function GroupPreviewList({ groups }: { groups: PreviewGroup[] }) {
  if (groups.length === 0) {
    return <EmptyState title="No study groups yet" description="Create the first one after you sign up." />;
  }
  return (
    <div className="grid gap-4 md:grid-cols-3">
      {groups.map((group) => (
        <Card key={group.id} className="flex flex-col gap-3 p-5 hover:-translate-y-1 hover:shadow-lg">
          <div className="flex items-start justify-between gap-2">
            <h3 className="font-semibold leading-snug">{group.name}</h3>
            <Badge tone={group.mode === "ONLINE" ? "secondary" : "accent"}>{group.mode === "ONLINE" ? "Online" : "In person"}</Badge>
          </div>
          <p className="text-sm text-muted-foreground">{group.subject}</p>
          <p className="mt-auto flex items-center gap-2 text-sm text-muted-foreground">
            <Users className="size-4" />
            {group.memberCount}
            {group.maxMembers ? ` of ${group.maxMembers}` : ""} members
          </p>
        </Card>
      ))}
    </div>
  );
}

export function SkillCatalog({ skills }: { skills: Skill[] }) {
  const categories = Array.from(new Set(skills.map((skill) => skill.category)));
  return (
    <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
      {categories.map((category) => (
        <div key={category} className="rounded-2xl border border-border bg-card p-5">
          <h3 className="text-sm font-semibold uppercase tracking-wide text-muted-foreground">{category}</h3>
          <div className="mt-3 flex flex-wrap gap-2">
            {skills
              .filter((skill) => skill.category === category)
              .map((skill) => (
                <Badge key={skill.id} tone="neutral">
                  {skill.name}
                </Badge>
              ))}
          </div>
        </div>
      ))}
    </div>
  );
}

export function FinalCta() {
  return (
    <section className="mx-auto max-w-7xl px-4 pb-20 sm:px-6">
      <div className="flex flex-col items-center gap-6 rounded-3xl bg-gradient-to-br from-primary-600 to-secondary-700 px-6 py-14 text-center text-white shadow-xl dark:bg-primary-800 sm:px-12">
        <h2 className="text-3xl font-bold sm:text-4xl">Ready to trade skills with your classmates?</h2>
        <p className="max-w-xl text-primary-100">Create your free account with your campus email and start learning today.</p>
        <Link to="/signup">
          <Button size="lg" variant="secondary" className="border-transparent bg-white text-primary-800 hover:bg-primary-50 dark:bg-white dark:text-primary-900">
            Create your account
            <ArrowRight className="size-4" />
          </Button>
        </Link>
      </div>
    </section>
  );
}
