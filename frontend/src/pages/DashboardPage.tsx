import { Link } from "react-router-dom";
import { ArrowRight, BookOpen, CheckCircle2, Clock, GraduationCap, Users } from "lucide-react";
import { Bar, BarChart, CartesianGrid, Cell, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { PageHeader } from "../components/layout/PageHeader";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Card, CardHeader } from "../components/ui/card";
import { EmptyState, ErrorState } from "../components/ui/feedback";
import { GridSkeleton } from "../components/ui/skeleton";
import { useAsync } from "../hooks/useAsync";
import { formatDate, STATUS_LABELS } from "../lib/format";
import { dashboardApi } from "../services/api";
import type { RequestStatus } from "../types/api";


const STATUS_TONE: Record<RequestStatus, "warning" | "secondary" | "error" | "success"> = {
  PENDING: "warning",
  ACCEPTED: "secondary",
  REJECTED: "error",
  COMPLETED: "success",
};

const CHART_COLORS = ["var(--color-warning-500)", "var(--color-secondary-500)", "var(--color-error-500)", "var(--color-success-500)"];

export function DashboardPage() {
  const { data, error, loading, reload } = useAsync(() => dashboardApi.get(), []);

  if (loading) return <GridSkeleton count={3} />;
  if (error || !data) return <ErrorState message={error ?? "Could not load your dashboard."} onRetry={reload} />;

  const stats = [
    { label: "Pending requests", value: data.pendingRequests, icon: Clock },
    { label: "Completed exchanges", value: data.completedExchanges, icon: CheckCircle2 },
    { label: "Study groups", value: data.activeGroups, icon: Users },
  ];
  const chart = [
    { name: "Pending", value: data.requestStats.pending },
    { name: "Accepted", value: data.requestStats.accepted },
    { name: "Rejected", value: data.requestStats.rejected },
    { name: "Completed", value: data.requestStats.completed },
  ];
  const hasRequests = chart.some((c) => c.value > 0);

  return (
    <>
      <PageHeader
        title={`Hi, ${data.name.split(" ")[0]}`}
        description="Here's what's happening with your skill swaps."
        actions={
          <Link to="/tutors">
            <Button>
              Find a tutor
              <ArrowRight className="size-4" />
            </Button>
          </Link>
        }
      />

      <div className="grid gap-5 sm:grid-cols-3">
        {stats.map((s) => (
          <Card key={s.label} className="flex items-center gap-4 p-6 animate-fade-up">
            <span className="flex size-12 items-center justify-center rounded-xl bg-primary-100 text-primary-800 dark:bg-primary-900/60 dark:text-primary-100">
              <s.icon className="size-6" />
            </span>
            <div>
              <p className="text-3xl font-bold">{s.value}</p>
              <p className="text-sm text-muted-foreground">{s.label}</p>
            </div>
          </Card>
        ))}
      </div>

      <div className="mt-8 grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader title="My skills" action={<Link to="/profile" className="text-sm font-semibold text-primary-700 hover:underline dark:text-primary-300">Edit</Link>} />
          <div className="space-y-5 p-6">
            <SkillRow icon={<GraduationCap className="size-4" />} label="I can teach" skills={data.teachSkills.map((s) => s.name)} tone="primary" />
            <SkillRow icon={<BookOpen className="size-4" />} label="I want to learn" skills={data.learnSkills.map((s) => s.name)} tone="secondary" />
          </div>
        </Card>

        <Card>
          <CardHeader title="Request overview" description="All swaps you've sent or received." />
          <div className="h-64 p-4">
            {hasRequests ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chart}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
                  <XAxis dataKey="name" tick={{ fill: "var(--muted-foreground)", fontSize: 12 }} axisLine={false} tickLine={false} />
                  <YAxis allowDecimals={false} tick={{ fill: "var(--muted-foreground)", fontSize: 12 }} axisLine={false} tickLine={false} />
                  <Tooltip cursor={{ fill: "var(--muted)" }} contentStyle={{ background: "var(--card)", border: "1px solid var(--border)", borderRadius: 12 }} />
                  <Bar dataKey="value" radius={[8, 8, 0, 0]}>
                    {chart.map((_, i) => (
                      <Cell key={i} fill={CHART_COLORS[i]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="flex h-full items-center justify-center text-sm text-muted-foreground">No requests yet.</div>
            )}
          </div>
        </Card>
      </div>

      <Card className="mt-6">
        <CardHeader title="Recent activity" action={<Link to="/requests" className="text-sm font-semibold text-primary-700 hover:underline dark:text-primary-300">View all</Link>} />
        <div className="p-6">
          {data.recentActivity.length === 0 ? (
            <EmptyState title="No activity yet" description="Send your first swap request from Find Tutors." />
          ) : (
            <ul className="divide-y divide-border">
              {data.recentActivity.map((a) => (
                <li key={a.requestId} className="flex flex-wrap items-center justify-between gap-3 py-3">
                  <div>
                    <p className="text-sm font-medium">{a.message}</p>
                    <p className="text-xs text-muted-foreground">{formatDate(a.date)}</p>
                  </div>
                  <Badge tone={STATUS_TONE[a.status]}>{STATUS_LABELS[a.status]}</Badge>
                </li>
              ))}
            </ul>
          )}
        </div>
      </Card>
    </>
  );
}

function SkillRow({ icon, label, skills, tone }: { icon: React.ReactNode; label: string; skills: string[]; tone: "primary" | "secondary" }) {
  return (
    <div>
      <p className="mb-2 flex items-center gap-2 text-sm font-semibold text-muted-foreground">
        {icon}
        {label}
      </p>
      {skills.length === 0 ? (
        <p className="text-sm text-muted-foreground">
          None yet.{" "}
          <Link to="/profile" className="font-medium text-primary-700 hover:underline dark:text-primary-300">
            Add skills
          </Link>
        </p>
      ) : (
        <div className="flex flex-wrap gap-2">
          {skills.map((s) => (
            <Badge key={s} tone={tone}>
              {s}
            </Badge>
          ))}
        </div>
      )}
    </div>
  );
}
