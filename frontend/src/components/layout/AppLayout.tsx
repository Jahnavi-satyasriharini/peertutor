import { useState } from "react";
import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { LogOut, Menu, X } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";
import { useToast } from "../../hooks/useToast";
import { cn } from "../../lib/utils";
import { initials } from "../../lib/format";
import { Button } from "../ui/button";
import { ToastViewport } from "../ui/toast-viewport";
import { ThemeToggle } from "./ThemeToggle";

const NAV_ITEMS = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/tutors", label: "Find Tutors" },
  { to: "/requests", label: "My Requests" },
  { to: "/groups", label: "Study Groups" },
  { to: "/profile", label: "Profile" },
];

export function AppLayout() {
  const { user, logout } = useAuth();
  const { notify } = useToast();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const items = user?.role === "ADMIN" ? [...NAV_ITEMS, { to: "/admin", label: "Admin" }] : NAV_ITEMS;

  const handleLogout = async () => {
    try {
      await logout();
      navigate("/", { replace: true });
    } catch {
      notify({ tone: "error", title: "Could not log out", description: "Please try again." });
    }
  };

  return (
    <div className="flex min-h-dvh flex-col">
      <header className="sticky top-0 z-40 border-b border-border bg-background/85 backdrop-blur-md">
        <div className="mx-auto flex h-16 max-w-7xl items-center justify-between gap-4 px-4 sm:px-6">
          <Link to="/dashboard" className="flex items-center gap-2 font-semibold tracking-tight">
            <span className="flex size-8 items-center justify-center rounded-lg bg-primary-600 text-white dark:bg-primary-500 dark:text-slate-950">
              CS
            </span>
            <span className="hidden sm:inline">CampusSkill</span>
          </Link>

          <nav aria-label="Main" className="hidden items-center gap-1 lg:flex">
            {items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  cn(
                    "rounded-lg px-3 py-2 text-sm font-medium transition-colors duration-200",
                    isActive
                      ? "bg-primary-100 text-primary-800 dark:bg-primary-900/60 dark:text-primary-100"
                      : "text-muted-foreground hover:bg-muted hover:text-foreground",
                  )
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="flex items-center gap-2">
            <ThemeToggle />
            <div className="hidden items-center gap-3 border-l border-border pl-3 md:flex">
              <div className="flex items-center gap-2">
                <span className="flex size-8 items-center justify-center rounded-full bg-secondary-100 text-xs font-semibold text-secondary-800 dark:bg-secondary-900 dark:text-secondary-100">
                  {user ? initials(user.name) : ""}
                </span>
                <span className="max-w-32 truncate text-sm font-medium">{user?.name}</span>
              </div>
              <Button variant="ghost" size="sm" onClick={handleLogout} aria-label="Log out">
                <LogOut className="size-4" />
                Log out
              </Button>
            </div>
            <button
              type="button"
              className="rounded-lg p-2 text-foreground transition-colors hover:bg-muted lg:hidden"
              onClick={() => setMenuOpen((open) => !open)}
              aria-expanded={menuOpen}
              aria-controls="mobile-nav"
              aria-label={menuOpen ? "Close menu" : "Open menu"}
            >
              {menuOpen ? <X className="size-5" /> : <Menu className="size-5" />}
            </button>
          </div>
        </div>

        {menuOpen && (
          <nav id="mobile-nav" aria-label="Mobile" className="border-t border-border bg-card px-4 py-3 animate-fade-in lg:hidden">
            <div className="flex flex-col gap-1">
              {items.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  onClick={() => setMenuOpen(false)}
                  className={({ isActive }) =>
                    cn(
                      "rounded-lg px-3 py-2.5 text-sm font-medium",
                      isActive ? "bg-primary-100 text-primary-800 dark:bg-primary-900/60 dark:text-primary-100" : "text-foreground hover:bg-muted",
                    )
                  }
                >
                  {item.label}
                </NavLink>
              ))}
              <div className="mt-2 flex items-center justify-between border-t border-border pt-3">
                <span className="text-sm text-muted-foreground">{user?.name}</span>
                <Button variant="ghost" size="sm" onClick={handleLogout}>
                  <LogOut className="size-4" />
                  Log out
                </Button>
              </div>
            </div>
          </nav>
        )}
      </header>

      <main className="mx-auto w-full max-w-7xl flex-1 px-4 py-8 sm:px-6 sm:py-10">
        <Outlet />
      </main>

      <footer className="border-t border-border py-6 text-center text-sm text-muted-foreground">
        CampusSkill: Learn together, grow together.
      </footer>
      <ToastViewport />
    </div>
  );
}
