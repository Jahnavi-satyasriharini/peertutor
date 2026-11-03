import { useState } from "react";
import { Link } from "react-router-dom";
import { useParams } from "react-router-dom";
import { ArrowLeft, Users } from "lucide-react";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Card, CardHeader } from "../components/ui/card";
import { ConfirmDialog } from "../components/ui/confirm-dialog";
import { ErrorState } from "../components/ui/feedback";
import { CardSkeleton } from "../components/ui/skeleton";
import { errorMessage, useAsync } from "../hooks/useAsync";
import { useToast } from "../hooks/useToast";
import { initials, yearLabel } from "../lib/format";
import { groupsApi } from "../services/api";


export function GroupDetailPage() {
  const { id = "" } = useParams();
  const groupId = Number(id);
  const { notify } = useToast();
  const detail = useAsync(() => groupsApi.get(groupId), [groupId]);
  const [busy, setBusy] = useState(false);
  const [confirmLeave, setConfirmLeave] = useState(false);

  if (detail.loading) return <CardSkeleton lines={6} />;
  if (detail.error || !detail.data) return <ErrorState message={detail.error ?? "Study group not found."} onRetry={detail.reload} />;
  const { group, members } = detail.data;

  const act = async (fn: () => ReturnType<typeof groupsApi.join>, title: string) => {
    setBusy(true);
    try {
      detail.setData(await fn());
      notify({ tone: "success", title });
    } catch (err) {
      notify({ tone: "error", title: "Something went wrong", description: errorMessage(err) });
    } finally {
      setBusy(false);
      setConfirmLeave(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl">
      <Link to="/groups" className="mb-6 inline-flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-foreground">
        <ArrowLeft className="size-4" />
        All groups
      </Link>
      <Card className="p-6 sm:p-8 animate-fade-up">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold">{group.name}</h1>
            <p className="mt-1 text-muted-foreground">{group.subject}</p>
          </div>
          <Badge tone={group.mode === "ONLINE" ? "secondary" : "accent"}>{group.mode === "ONLINE" ? "Online" : "In person"}</Badge>
        </div>
        {group.description && <p className="mt-5 leading-relaxed">{group.description}</p>}
        <div className="mt-6 flex flex-wrap items-center justify-between gap-4 border-t border-border pt-5">
          <p className="flex items-center gap-2 text-sm text-muted-foreground">
            <Users className="size-4" />
            {group.memberCount} of {group.maxMembers} members · Started by {group.owner ? "you" : group.creator.name}
          </p>
          {group.owner ? (
            <Badge tone="primary">You created this group</Badge>
          ) : group.joined ? (
            <Button variant="secondary" disabled={busy} onClick={() => setConfirmLeave(true)}>
              Leave group
            </Button>
          ) : (
            <Button disabled={group.full} loading={busy} onClick={() => act(() => groupsApi.join(groupId), "You joined the group")}>
              {group.full ? "Group is full" : "Join group"}
            </Button>
          )}
        </div>
      </Card>

      <Card className="mt-6">
        <CardHeader title="Members" />
        <ul className="divide-y divide-border">
          {members.map((m) => (
            <li key={m.id} className="flex items-center gap-3 px-6 py-3">
              <span className="flex size-9 items-center justify-center rounded-full bg-secondary-100 text-xs font-semibold text-secondary-800 dark:bg-secondary-900 dark:text-secondary-100">
                {initials(m.name)}
              </span>
              <div>
                <p className="text-sm font-medium">{m.name}</p>
                <p className="text-xs text-muted-foreground">{[m.branch, yearLabel(m.year)].filter(Boolean).join(" · ")}</p>
              </div>
              {m.id === group.creator.id && <Badge tone="primary" className="ml-auto">Creator</Badge>}
            </li>
          ))}
        </ul>
      </Card>

      <ConfirmDialog
        open={confirmLeave}
        title="Leave this group?"
        description="You can join again later if there's space."
        confirmLabel="Leave group"
        destructive
        loading={busy}
        onConfirm={() => act(() => groupsApi.leave(groupId), "You left the group")}
        onCancel={() => setConfirmLeave(false)}
      />
    </div>
  );
}
