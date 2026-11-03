import { Link } from "react-router-dom";
import heroImage from "../campus-hero.jpg";
import { Hero, LandingHeader } from "../components/landing/LandingHero";
import { FeatureGrid, FinalCta, GroupPreviewList, HowItWorks, SkillCatalog, TutorPreviewGrid } from "../components/landing/LandingSections";
import { GridSkeleton } from "../components/ui/skeleton";
import { ErrorState } from "../components/ui/feedback";
import { useAsync } from "../hooks/useAsync";
import { publicApi } from "../services/api";


export function LandingPage() {
  const preview = useAsync(() => publicApi.preview(), []);

  return (
    <div className="min-h-dvh">
      <LandingHeader />
      <main>
        <Hero imageUrl={heroImage} />
        <FeatureGrid />
        <HowItWorks />

        <section className="mx-auto max-w-7xl px-4 py-16 sm:px-6">
          <div className="mb-8">
            <h2 className="text-3xl font-bold sm:text-4xl">Top-rated peer tutors</h2>
            <p className="mt-2 text-muted-foreground">Students on your campus who are ready to share what they know.</p>
          </div>
          {preview.loading ? (
            <GridSkeleton count={3} />
          ) : preview.error ? (
            <ErrorState message={preview.error} onRetry={preview.reload} />
          ) : (
            <TutorPreviewGrid tutors={preview.data?.tutors ?? []} />
          )}
        </section>

        <section className="mx-auto max-w-7xl px-4 py-16 sm:px-6">
          <div className="mb-8">
            <h2 className="text-3xl font-bold sm:text-4xl">Popular study groups</h2>
            <p className="mt-2 text-muted-foreground">Join a group online or in person and learn alongside others.</p>
          </div>
          {preview.loading ? <GridSkeleton count={3} /> : <GroupPreviewList groups={preview.data?.groups ?? []} />}
        </section>

        <section id="skills" className="mx-auto max-w-7xl scroll-mt-20 px-4 py-16 sm:px-6">
          <div className="mb-8">
            <h2 className="text-3xl font-bold sm:text-4xl">Skills you can swap</h2>
            <p className="mt-2 text-muted-foreground">From programming to public speaking.</p>
          </div>
          {preview.loading ? <GridSkeleton count={3} /> : <SkillCatalog skills={preview.data?.skills ?? []} />}
        </section>

        <FinalCta />
      </main>
      <footer className="border-t border-border bg-card">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-10 sm:flex-row sm:items-center sm:justify-between sm:px-6">
          <div>
            <p className="font-semibold">CampusSkill</p>
            <p className="text-sm text-muted-foreground">Learn together, grow together.</p>
          </div>
          <nav className="flex gap-6 text-sm text-muted-foreground">
            <a href="#skills" className="hover:text-foreground">Skills</a>
            <Link to="/login" className="hover:text-foreground">Log in</Link>
            <Link to="/signup" className="hover:text-foreground">Sign up</Link>
          </nav>
          <p className="text-sm text-muted-foreground">© {new Date().getFullYear()} CampusSkill</p>
        </div>
      </footer>
    </div>
  );
}
