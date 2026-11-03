import { useState } from "react";
import { Link } from "react-router-dom";
import { Plus, Search, Users } from "lucide-react";
import { PageHeader } from "../components/layout/PageHeader";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { EmptyState, ErrorState } from "../components/ui/feedback";
import { Input, Select } from "../components/ui/form-controls";
import { GridSkeleton } from "../components/ui/skeleton";
import { useAsync } from "../hooks/useAsync";
import { useDebouncedValue } from "../hooks/useDebouncedValue";
import { groupsApi } from "../services/api";
import type { StudyMode } from "../types/api";


export function GroupsPage() {
  const [q, setQ] = useState("");
  const [mode, setMode] = useState<"" | StudyMode>("");
  const dq = useDebouncedValue(q);
  const groups = useAsync(() => groupsApi.list({ q: dq, mode: mode || undefined }), [dq, mode]);

  return (
    <>
      <PageHeader
        title="Study groups"
        description="Learn alongside classmates who share your goals."
        actions={
          <Link to="/groups/new">
            <Button>
              <Plus className="size-4" />
              Create group
            </Button>
          </Link>
        }
      />
      <div className="mb-8 flex flex-col gap-3 sm:flex-row">
        <div className="relative flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input aria-label="Search groups" className="pl-9" placeholder="Search by name or subject" value={q} onChange={(e) => setQ(e.target.value)} />
        </div>
        <Select aria-label="Mode" className="sm:w-48" value={mode} onChange={(e) => setMode(e.target.value as "" | StudyMode)}>
          <option value="">All modes</option>
          <option value="ONLINE">Online</option>
          <option value="OFFLINE">In person</option>
        </Select>
      </div>

      {groups.loading ? (
        <GridSkeleton />
      ) : groups.error ? (
        <ErrorState message={groups.error} onRetry={groups.reload} />
      ) : (groups.data ?? []).length === 0 ? (
        <EmptyState title="No groups found" description="Start one and invite your classmates." />
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
          {groups.data!.map((g) => (
            <Link key={g.id} to={`/groups/${g.id}`} className="block">
              <Card className="flex h-full flex-col gap-3 p-6 hover:-translate-y-1 hover:shadow-lg">
                <div className="flex items-start justify-between gap-2">
                  <h3 className="font-semibold leading-snug">{g.name}</h3>
                  <Badge tone={g.mode === "ONLINE" ? "secondary" : "accent"}>{g.mode === "ONLINE" ? "Online" : "In person"}</Badge>
                </div>
                <p className="text-sm text-muted-foreground">{g.subject}</p>
                {g.description && <p className="line-clamp-2 text-sm text-muted-foreground">{g.description}</p>}
                <div className="mt-auto flex items-center justify-between pt-2 text-sm">
                  <span className="flex items-center gap-2 text-muted-foreground">
                    <Users className="size-4" />
                    {g.memberCount} of {g.maxMembers}
                  </span>
                  {g.owner ? <Badge tone="primary">Your group</Badge> : g.joined ? <Badge tone="success">Joined</Badge> : g.full ? <Badge tone="error">Full</Badge> : null}
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </>
  );
}
