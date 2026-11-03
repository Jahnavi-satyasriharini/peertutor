import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { PageHeader } from "../components/layout/PageHeader";
import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { InlineAlert } from "../components/ui/feedback";
import { Field, Input, Select, Textarea } from "../components/ui/form-controls";
import { errorMessage } from "../hooks/useAsync";
import { useToast } from "../hooks/useToast";
import { groupsApi } from "../services/api";
import type { StudyMode } from "../types/api";


export function CreateGroupPage() {
  const navigate = useNavigate();
  const { notify } = useToast();
  const [form, setForm] = useState({ name: "", subject: "", description: "", maxMembers: "6", mode: "ONLINE" as StudyMode });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    const errs: Record<string, string> = {};
    if (!form.name.trim()) errs.name = "Give your group a name.";
    if (!form.subject.trim()) errs.subject = "Enter a subject.";
    const max = Number(form.maxMembers);
    if (!Number.isInteger(max) || max < 2 || max > 100) errs.maxMembers = "Choose between 2 and 100 members.";
    if (form.description.length > 1000) errs.description = "Keep it under 1000 characters.";
    setErrors(errs);
    if (Object.keys(errs).length) return;
    setSaving(true);
    setFormError(null);
    try {
      const created = await groupsApi.create({ ...form, maxMembers: max });
      notify({ tone: "success", title: "Group created" });
      navigate(`/groups/${created.id}`);
    } catch (err) {
      setFormError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mx-auto max-w-2xl">
      <PageHeader title="Create a study group" description="You'll be added as the first member." />
      <Card className="p-6">
        <form onSubmit={submit} noValidate className="space-y-5">
          {formError && <InlineAlert>{formError}</InlineAlert>}
          <Field label="Group name" htmlFor="g-name" error={errors.name}>
            <Input id="g-name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} invalid={Boolean(errors.name)} />
          </Field>
          <Field label="Subject" htmlFor="g-subject" error={errors.subject}>
            <Input id="g-subject" placeholder="Data Structures" value={form.subject} onChange={(e) => setForm({ ...form, subject: e.target.value })} invalid={Boolean(errors.subject)} />
          </Field>
          <Field label="Description (optional)" htmlFor="g-desc" error={errors.description}>
            <Textarea id="g-desc" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          </Field>
          <div className="grid gap-5 sm:grid-cols-2">
            <Field label="Maximum members" htmlFor="g-max" error={errors.maxMembers}>
              <Input id="g-max" type="number" min={2} max={100} value={form.maxMembers} onChange={(e) => setForm({ ...form, maxMembers: e.target.value })} invalid={Boolean(errors.maxMembers)} />
            </Field>
            <Field label="Mode" htmlFor="g-mode">
              <Select id="g-mode" value={form.mode} onChange={(e) => setForm({ ...form, mode: e.target.value as StudyMode })}>
                <option value="ONLINE">Online</option>
                <option value="OFFLINE">In person</option>
              </Select>
            </Field>
          </div>
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => navigate("/groups")}>
              Cancel
            </Button>
            <Button type="submit" loading={saving}>
              Create group
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}
