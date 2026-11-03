import { useState } from "react";
import { Search, Send } from "lucide-react";
import { PageHeader } from "../components/layout/PageHeader";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { EmptyState, ErrorState } from "../components/ui/feedback";
import { Field, Input, Select } from "../components/ui/form-controls";
import { Modal } from "../components/ui/modal";
import { GridSkeleton } from "../components/ui/skeleton";
import { RatingSummary } from "../components/ui/star-rating";
import { errorMessage, useAsync } from "../hooks/useAsync";
import { useDebouncedValue } from "../hooks/useDebouncedValue";
import { useToast } from "../hooks/useToast";
import { initials, yearLabel } from "../lib/format";
import { requestsApi, tutorsApi } from "../services/api";
import type { Tutor } from "../types/api";


export function TutorsPage() {
  const [skill, setSkill] = useState("");
  const [branch, setBranch] = useState("");
  const [year, setYear] = useState("");
  const dSkill = useDebouncedValue(skill);
  const dBranch = useDebouncedValue(branch);
  const tutors = useAsync(() => tutorsApi.search({ skill: dSkill, branch: dBranch, year: year ? Number(year) : undefined }), [dSkill, dBranch, year]);
  const [target, setTarget] = useState<Tutor | null>(null);

  return (
    <>
      <PageHeader title="Find tutors" description="Search for classmates who teach the skill you want to learn." />
      <Card className="mb-8 grid gap-4 p-5 sm:grid-cols-[2fr_1.5fr_1fr]">
        <Field label="Skill" htmlFor="f-skill">
          <div className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
            <Input id="f-skill" className="pl-9" placeholder="e.g. Python" value={skill} onChange={(e) => setSkill(e.target.value)} />
          </div>
        </Field>
        <Field label="Branch" htmlFor="f-branch">
          <Input id="f-branch" placeholder="e.g. CSE" value={branch} onChange={(e) => setBranch(e.target.value)} />
        </Field>
        <Field label="Year" htmlFor="f-year">
          <Select id="f-year" value={year} onChange={(e) => setYear(e.target.value)}>
            <option value="">Any year</option>
            {[1, 2, 3, 4, 5].map((y) => (
              <option key={y} value={y}>
                Year {y}
              </option>
            ))}
          </Select>
        </Field>
      </Card>

      {tutors.loading ? (
        <GridSkeleton />
      ) : tutors.error ? (
        <ErrorState message={tutors.error} onRetry={tutors.reload} />
      ) : (tutors.data ?? []).length === 0 ? (
        <EmptyState title="No tutors found" description="Try a different skill or clear the filters." />
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
          {tutors.data!.map((t) => (
            <Card key={t.id} className="flex flex-col p-6 hover:-translate-y-1 hover:shadow-lg">
              <div className="flex items-center gap-3">
                <span className="flex size-11 items-center justify-center rounded-full bg-secondary-100 font-semibold text-secondary-800 dark:bg-secondary-900 dark:text-secondary-100">
                  {initials(t.name)}
                </span>
                <div className="min-w-0">
                  <p className="truncate font-semibold">{t.name}</p>
                  <p className="truncate text-sm text-muted-foreground">
                    {[t.branch, yearLabel(t.year)].filter(Boolean).join(" · ")}
                  </p>
                </div>
              </div>
              <div className="mt-3">
                <RatingSummary average={t.averageRating} count={t.ratingCount} />
              </div>
              {t.bio && <p className="mt-3 line-clamp-2 text-sm text-muted-foreground">{t.bio}</p>}
              <div className="mt-4 flex flex-wrap gap-2">
                {t.teachSkills.map((s) => (
                  <Badge key={s.id} tone="primary">
                    {s.name}
                  </Badge>
                ))}
              </div>
              <Button className="mt-5 w-full" onClick={() => setTarget(t)}>
                <Send className="size-4" />
                Request a swap
              </Button>
            </Card>
          ))}
        </div>
      )}

      {target && <RequestModal tutor={target} onClose={() => setTarget(null)} />}
    </>
  );
}

function RequestModal({ tutor, onClose }: { tutor: Tutor; onClose: () => void }) {
  const { notify } = useToast();
  const [skillId, setSkillId] = useState(String(tutor.teachSkills[0]?.id ?? ""));
  const [sending, setSending] = useState(false);

  const send = async () => {
    setSending(true);
    try {
      await requestsApi.create(tutor.id, Number(skillId));
      notify({ tone: "success", title: "Request sent", description: `${tutor.name.split(" ")[0]} will see it in their requests.` });
      onClose();
    } catch (err) {
      notify({ tone: "error", title: "Could not send request", description: errorMessage(err) });
    } finally {
      setSending(false);
    }
  };

  return (
    <Modal
      open
      onClose={onClose}
      title={`Learn from ${tutor.name}`}
      description="Choose the skill you'd like them to teach you."
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={sending}>
            Cancel
          </Button>
          <Button onClick={send} loading={sending} disabled={!skillId}>
            Send request
          </Button>
        </>
      }
    >
      <Field label="Skill" htmlFor="req-skill">
        <Select id="req-skill" value={skillId} onChange={(e) => setSkillId(e.target.value)}>
          {tutor.teachSkills.map((s) => (
            <option key={s.id} value={s.id}>
              {s.name}
            </option>
          ))}
        </Select>
      </Field>
    </Modal>
  );
}
