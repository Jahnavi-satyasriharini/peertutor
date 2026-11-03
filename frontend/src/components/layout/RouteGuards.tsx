import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { Skeleton } from "../ui/skeleton";
import { EmptyState } from "../ui/feedback";
import { ShieldAlert } from "lucide-react";

function SessionLoading() {
  return (
    <div className="flex min-h-dvh items-center justify-center px-4" aria-busy="true">
      <div className="w-full max-w-md space-y-4">
        <Skeleton className="mx-auto h-10 w-40" />
        <Skeleton className="h-64 w-full rounded-2xl" />
      </div>
    </div>
  );
}

export function RequireAuth() {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return <SessionLoading />;
  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  return <Outlet />;
}

export function RequireGuest() {
  const { user, loading } = useAuth();
  if (loading) return <SessionLoading />;
  if (user) return <Navigate to="/dashboard" replace />;
  return <Outlet />;
}

export function RequireAdmin() {
  const { user } = useAuth();
  if (user?.role !== "ADMIN") {
    return (
      <EmptyState
        icon={<ShieldAlert className="size-7" />}
        title="Admin access required"
        description="Only administrators can view this page."
      />
    );
  }
  return <Outlet />;
}
