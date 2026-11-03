import { useState } from "react";
import { Repeat, Search, Trash2, Users, UsersRound } from "lucide-react";
import { PageHeader } from "../components/layout/PageHeader";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { ConfirmDialog } from "../components/ui/confirm-dialog";
import { EmptyState, ErrorState } from "../components/ui/feedback";
import { Input } from "../components/ui/form-controls";
import { GridSkeleton } from "../components/ui/skeleton";
import { errorMessage, useAsync } from "../hooks/useAsync";
import { useAuth } from "../hooks/useAuth";
import { useDebouncedValue } from "../hooks/useDebouncedValue";
import { useToast } from "../hooks/useToast";
import { ROLE_LABELS, yearLabel } from "../lib/format";
import { adminApi } from "../services/api";
import type { User } from "../types/api";


export function AdminPage() {
  const { user: me } = useAuth();
  const { notify } = useToast();
  const [q, setQ] = useState("");
  const dq = useDebouncedValue(q);
  const stats = useAsync(() => adminApi.stats(), []);
  const users = useAsync(() => adminApi.users(dq), [dq]);
  const [target, setTarget] = useState<User | null>(null);
  const [deleting, setDeleting] = useState(false);

  const remove = async () => {
    if (!target) return;
    setDeleting(true);
    try {
      await adminApi.deleteUser(target.id);
      notify({ tone: "success", title: "User deleted", description: `${target.name} and their data were removed.` });
      setTarget(null);
      users.reload();
      stats.reload();
    } catch (err) {
      notify({ tone: "error", title: "Could not delete user", description: errorMessage(err) });
    } finally {
      setDeleting(false);
    }
  };

  const cards = [
    { label: "Users", value: stats.data?.totalUsers, icon: Users },
    { label: "Study groups", value: stats.data?.totalGroups, icon: UsersRound },
    { label: "Swap requests", value: stats.data?.totalSwapRequests, icon: Repeat },
  ];

  return (
    <>
      <PageHeader title="Admin" description="Keep CampusSkill running smoothly." />
      {stats.error ? (
        <ErrorState message={stats.error} onRetry={stats.reload} />
      ) : (
        <div className="grid gap-5 sm:grid-cols-3">
          {cards.map((c) => (
            <Card key={c.label} className="flex items-center gap-4 p-6">
              <span className="flex size-12 items-center justify-center rounded-xl bg-accent-100 text-accent-800 dark:bg-accent-900/50 dark:text-accent-100">
                <c.icon className="size-6" />
              </span>
              <div>
                <p className="text-3xl font-bold">{stats.loading ? "–" : c.value}</p>
                <p className="text-sm text-muted-foreground">{c.label}</p>
              </div>
            </Card>
          ))}
        </div>
      )}

      <div className="relative mt-8 mb-4 max-w-md">
        <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
        <Input aria-label="Search users" className="pl-9" placeholder="Search by name or email" value={q} onChange={(e) => setQ(e.target.value)} />
      </div>

      {users.loading ? (
        <GridSkeleton count={3} />
      ) : users.error ? (
        <ErrorState message={users.error} onRetry={users.reload} />
      ) : (users.data ?? []).length === 0 ? (
        <EmptyState title="No users found" />
      ) : (
        <Card className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-border text-muted-foreground">
              <tr>
                <th className="px-5 py-3 font-medium">Name</th>
                <th className="px-5 py-3 font-medium">Email</th>
                <th className="px-5 py-3 font-medium">Branch</th>
                <th className="px-5 py-3 font-medium">Role</th>
                <th className="px-5 py-3" />
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {users.data!.map((u) => (
                <tr key={u.id}>
                  <td className="px-5 py-3 font-medium">{u.name}</td>
                  <td className="px-5 py-3 text-muted-foreground">{u.email}</td>
                  <td className="px-5 py-3 text-muted-foreground">{[u.branch, yearLabel(u.year)].filter(Boolean).join(" · ")}</td>
                  <td className="px-5 py-3">
                    <Badge tone={u.role === "ADMIN" ? "accent" : "neutral"}>{ROLE_LABELS[u.role]}</Badge>
                  </td>
                  <td className="px-5 py-3 text-right">
                    {u.id !== me?.id && (
                      <Button size="sm" variant="ghost" aria-label={`Delete ${u.name}`} onClick={() => setTarget(u)}>
                        <Trash2 className="size-4" />
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      <ConfirmDialog
        open={Boolean(target)}
        title={`Delete ${target?.name ?? "user"}?`}
        description="This removes their account, skills, requests, memberships and any groups they created."
        confirmLabel="Delete user"
        destructive
        loading={deleting}
        onConfirm={remove}
        onCancel={() => setTarget(null)}
      />
    </>
  );
}
