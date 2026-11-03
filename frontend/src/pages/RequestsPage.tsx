import { useState } from "react";
import { PageHeader } from "../components/layout/PageHeader";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { EmptyState, ErrorState } from "../components/ui/feedback";
import { GridSkeleton } from "../components/ui/skeleton";
import { StarRating } from "../components/ui/star-rating";
import { errorMessage, useAsync } from "../hooks/useAsync";
import { useToast } from "../hooks/useToast";
import { cn } from "../lib/utils";
import { formatDate, initials, STATUS_LABELS } from "../lib/format";
import { requestsApi } from "../services/api";
import type { RequestStatus, SwapRequest } from "../types/api";


const STATUS_TONE: Record<RequestStatus, "warning" | "secondary" | "error" | "success"> = {
  PENDING: "warning",
  ACCEPTED: "secondary",
  REJECTED: "error",
  COMPLETED: "success",
};

export function RequestsPage() {
  const [tab, setTab] = useState<"incoming" | "outgoing">("incoming");
  const list = useAsync(() => (tab === "incoming" ? requestsApi.incoming() : requestsApi.outgoing()), [tab]);

  return (
    <>
      <PageHeader title="My requests" description="Manage the skill swaps you've received and sent." />
      <div role="tablist" className="mb-6 inline-flex rounded-xl border border-border bg-card p-1">
        {(["incoming", "outgoing"] as const).map((t) => (
          <button
            key={t}
            role="tab"
            aria-selected={tab === t}
            onClick={() => setTab(t)}
            className={cn(
              "rounded-lg px-4 py-2 text-sm font-medium transition-colors",
              tab === t ? "bg-primary-600 text-white dark:bg-primary-500 dark:text-slate-950" : "text-muted-foreground hover:text-foreground",
            )}
          >
            {t === "incoming" ? "Received" : "Sent"}
          </button>
        ))}
      </div>

      {list.loading ? (
        <GridSkeleton count={3} />
      ) : list.error ? (
        <ErrorState message={list.error} onRetry={list.reload} />
      ) : (list.data ?? []).length === 0 ? (
        <EmptyState
          title={tab === "incoming" ? "No requests received" : "No requests sent"}
          description={tab === "incoming" ? "Add skills you can teach so classmates can find you." : "Find a tutor and send your first request."}
        />
      ) : (
        <div className="space-y-4">
          {list.data!.map((r) => (
            <RequestCard key={r.id} request={r} incoming={tab === "incoming"} onChanged={list.reload} />
          ))}
        </div>
      )}
    </>
  );
}

function RequestCard({ request, incoming, onChanged }: { request: SwapRequest; incoming: boolean; onChanged: () => void }) {
  const { notify } = useToast();
  const [busy, setBusy] = useState(false);
  const other = incoming ? request.requester : request.receiver;

  const act = async (fn: () => Promise<unknown>, title: string) => {
    setBusy(true);
    try {
      await fn();
      notify({ tone: "success", title });
      onChanged();
    } catch (err) {
      notify({ tone: "error", title: "Something went wrong", description: errorMessage(err) });
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card className="flex flex-col gap-4 p-5 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex items-center gap-3">
        <span className="flex size-11 shrink-0 items-center justify-center rounded-full bg-secondary-100 font-semibold text-secondary-800 dark:bg-secondary-900 dark:text-secondary-100">
          {initials(other.name)}
        </span>
        <div>
          <p className="font-semibold">
            {incoming ? `${other.name} wants to learn ${request.skill.name}` : `You asked ${other.name} for ${request.skill.name}`}
          </p>
          <p className="text-sm text-muted-foreground">
            {[other.branch, formatDate(request.date)].filter(Boolean).join(" · ")}
          </p>
        </div>
      </div>
      <div className="flex flex-wrap items-center gap-2">
        <Badge tone={STATUS_TONE[request.status]}>{STATUS_LABELS[request.status]}</Badge>
        {incoming && request.status === "PENDING" && (
          <>
            <Button size="sm" disabled={busy} onClick={() => act(() => requestsApi.updateStatus(request.id, "ACCEPTED"), "Request accepted")}>
              Accept
            </Button>
            <Button size="sm" variant="secondary" disabled={busy} onClick={() => act(() => requestsApi.updateStatus(request.id, "REJECTED"), "Request declined")}>
              Decline
            </Button>
          </>
        )}
        {request.status === "ACCEPTED" && (
          <Button size="sm" disabled={busy} onClick={() => act(() => requestsApi.updateStatus(request.id, "COMPLETED"), "Marked as completed")}>
            Mark completed
          </Button>
        )}
        {request.status === "COMPLETED" &&
          (request.rating !== null ? (
            <StarRating value={request.rating} size="sm" />
          ) : !incoming ? (
            <span className="flex items-center gap-2 text-sm text-muted-foreground">
              Rate:
              <StarRating value={null} label="Rate this exchange" onChange={(v) => !busy && act(() => requestsApi.rate(request.id, v), "Thanks for rating!")} />
            </span>
          ) : null)}
      </div>
    </Card>
  );
}
