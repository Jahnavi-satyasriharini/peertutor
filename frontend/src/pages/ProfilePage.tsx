import { useEffect, useState, type FormEvent } from "react";
import { X } from "lucide-react";
import { PageHeader } from "../components/layout/PageHeader";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Card, CardHeader } from "../components/ui/card";
import { ErrorState, InlineAlert } from "../components/ui/feedback";
import { Field, Input, Select, Textarea } from "../components/ui/form-controls";
import { CardSkeleton } from "../components/ui/skeleton";
import { RatingSummary } from "../components/ui/star-rating";
import { errorMessage, useAsync } from "../hooks/useAsync";
import { useAuth } from "../hooks/useAuth";
import { useToast } from "../hooks/useToast";
import { initials, ROLE_LABELS } from "../lib/format";
import { profileApi, skillsApi } from "../services/api";
import type { Profile, Skill, SkillType, UserSkill } from "../types/api";


export function ProfilePage() {
  const profile = useAsync(() => profileApi.get(), []);
  const skills = useAsync(() => skillsApi.list(), []);

  if (profile.loading) return <div className="grid gap-6 lg:grid-cols-2"><CardSkeleton lines={6} /><CardSkeleton lines={6} /></div>;
  if (profile.error || !profile.data) return <ErrorState message={profile.error ?? "Could not load your profile."} onRetry={profile.reload} />;

  return (
    <>
      <PageHeader title="My profile" description="Tell classmates who you are and what you can swap." />
      <div className="grid gap-6 lg:grid-cols-[1fr_1.1fr]">
        <DetailsCard profile={profile.data} onSaved={profile.setData} />
        <div className="space-y-6">
          <SkillsCard type="TEACH" title="Skills I can teach" profile={profile.data} catalog={skills.data ?? []} onChange={profile.setData} />
          <SkillsCard type="LEARN" title="Skills I want to learn" profile={profile.data} catalog={skills.data ?? []} onChange={profile.setData} />
        </div>
      </div>
    </>
  );
}

function DetailsCard({ profile, onSaved }: { profile: Profile; onSaved: (p: Profile) => void }) {
  const { notify } = useToast();
  const { refresh } = useAuth();
  const [form, setForm] = useState({ name: profile.name, branch: profile.branch ?? "", year: String(profile.year ?? 1), bio: profile.bio ?? "" });
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    setForm({ name: profile.name, branch: profile.branch ?? "", year: String(profile.year ?? 1), bio: profile.bio ?? "" });
  }, [profile.name, profile.branch, profile.year, profile.bio]);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    if (!form.name.trim()) return setError("Tell us your name.");
    if (!form.branch.trim()) return setError("Enter your branch or major.");
    if (form.bio.length > 500) return setError("Keep your bio under 500 characters.");
    setError(null);
    setSaving(true);
    try {
      onSaved(await profileApi.update({ name: form.name, branch: form.branch, year: Number(form.year), bio: form.bio }));
      await refresh();
      notify({ tone: "success", title: "Profile saved" });
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <Card>
      <div className="flex items-center gap-4 border-b border-border px-6 py-5">
        <span className="flex size-14 items-center justify-center rounded-full bg-secondary-100 text-lg font-semibold text-secondary-800 dark:bg-secondary-900 dark:text-secondary-100">
          {initials(profile.name)}
        </span>
        <div className="min-w-0">
          <p className="truncate font-semibold">{profile.email}</p>
          <div className="mt-1 flex flex-wrap items-center gap-2">
            <Badge tone="neutral">{ROLE_LABELS[profile.role]}</Badge>
            <RatingSummary average={profile.averageRating} count={profile.ratingCount} />
          </div>
        </div>
      </div>
      <form onSubmit={submit} className="space-y-5 p-6" noValidate>
        {error && <InlineAlert>{error}</InlineAlert>}
        <Field label="Full name" htmlFor="p-name">
          <Input id="p-name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        </Field>
        <div className="grid gap-5 sm:grid-cols-2">
          <Field label="Branch or major" htmlFor="p-branch">
            <Input id="p-branch" value={form.branch} onChange={(e) => setForm({ ...form, branch: e.target.value })} />
          </Field>
          <Field label="Year" htmlFor="p-year">
            <Select id="p-year" value={form.year} onChange={(e) => setForm({ ...form, year: e.target.value })}>
              {[1, 2, 3, 4, 5].map((y) => (
                <option key={y} value={y}>
                  Year {y}
                </option>
              ))}
            </Select>
          </Field>
        </div>
        <Field label="Short bio" htmlFor="p-bio" hint={`${form.bio.length}/500`}>
          <Textarea id="p-bio" value={form.bio} onChange={(e) => setForm({ ...form, bio: e.target.value })} />
        </Field>
        <Button type="submit" loading={saving}>
          Save changes
        </Button>
      </form>
    </Card>
  );
}

function SkillsCard({ type, title, profile, catalog, onChange }: { type: SkillType; title: string; profile: Profile; catalog: Skill[]; onChange: (p: Profile) => void }) {
  const { notify } = useToast();
  const current: UserSkill[] = type === "TEACH" ? profile.teachSkills : profile.learnSkills;
  const available = catalog.filter((s) => !current.some((c) => c.skillId === s.id));
  const [selected, setSelected] = useState("");
  const [busy, setBusy] = useState(false);

  const run = async (action: () => Promise<Profile>) => {
    setBusy(true);
    try {
      onChange(await action());
      setSelected("");
    } catch (err) {
      notify({ tone: "error", title: "Could not update skills", description: errorMessage(err) });
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card>
      <CardHeader title={title} />
      <div className="space-y-4 p-6">
        {current.length === 0 ? (
          <p className="text-sm text-muted-foreground">No skills added yet.</p>
        ) : (
          <div className="flex flex-wrap gap-2">
            {current.map((s) => (
              <Badge key={s.id} tone={type === "TEACH" ? "primary" : "secondary"} className="pr-1">
                {s.name}
                <button type="button" aria-label={`Remove ${s.name}`} disabled={busy} onClick={() => run(() => profileApi.removeSkill(s.id))}
                  className="rounded-full p-0.5 hover:bg-foreground/10">
                  <X className="size-3" />
                </button>
              </Badge>
            ))}
          </div>
        )}
        <div className="flex gap-2">
          <Select aria-label={`Add a skill to ${title}`} value={selected} onChange={(e) => setSelected(e.target.value)}>
            <option value="">Choose a skill…</option>
            {available.map((s) => (
              <option key={s.id} value={s.id}>
                {s.name} · {s.category}
              </option>
            ))}
          </Select>
          <Button variant="secondary" disabled={!selected} loading={busy} onClick={() => run(() => profileApi.addSkill(Number(selected), type))}>
            Add
          </Button>
        </div>
      </div>
    </Card>
  );
}
