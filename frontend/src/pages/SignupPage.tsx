import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AuthShell } from "../components/layout/AuthShell";
import { Button } from "../components/ui/button";
import { Field, Input, Select, Textarea } from "../components/ui/form-controls";
import { InlineAlert } from "../components/ui/feedback";
import { useAuth } from "../hooks/useAuth";
import { useToast } from "../hooks/useToast";
import { errorMessage } from "../hooks/useAsync";
import { ApiError } from "../services/http";
import type { SignupInput } from "../types/api";

const EMPTY_FORM = { name: "", email: "", password: "", branch: "", year: "1", bio: "" };

type SignupForm = typeof EMPTY_FORM;

function validate(form: SignupForm): Record<string, string> {
  const errors: Record<string, string> = {};
  if (!form.name.trim()) errors.name = "Tell us your name.";
  if (!/^\S+@\S+\.\S+$/.test(form.email.trim())) errors.email = "Enter a valid college email.";
  if (form.password.length < 8) errors.password = "Use at least 8 characters.";
  if (form.password.length > 72) errors.password = "Use 72 characters or fewer.";
  if (!form.branch.trim()) errors.branch = "Enter your branch or major.";
  if (form.bio.length > 500) errors.bio = "Keep your bio under 500 characters.";
  return errors;
}

export function SignupPage() {
  const { signup } = useAuth();
  const { notify } = useToast();
  const navigate = useNavigate();

  const [form, setForm] = useState<SignupForm>(EMPTY_FORM);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const update = (key: keyof SignupForm) => (event: { target: { value: string } }) =>
    setForm((current) => ({ ...current, [key]: event.target.value }));

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const errors = validate(form);
    setFieldErrors(errors);
    setFormError(null);
    if (Object.keys(errors).length > 0) return;

    const input: SignupInput = {
      name: form.name.trim(),
      email: form.email.trim(),
      password: form.password,
      branch: form.branch.trim(),
      year: Number(form.year),
      bio: form.bio.trim() || undefined,
    };

    setSubmitting(true);
    try {
      await signup(input);
      notify({ tone: "success", title: "Account created", description: "Welcome to CampusSkill." });
      navigate("/profile", { replace: true });
    } catch (error) {
      if (error instanceof ApiError && Object.keys(error.fieldErrors).length > 0) {
        setFieldErrors(error.fieldErrors);
      }
      setFormError(errorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthShell title="Create your account" subtitle="Use your college email to join your campus community.">
      <form onSubmit={handleSubmit} noValidate className="space-y-5">
        {formError && <InlineAlert>{formError}</InlineAlert>}
        <Field label="Full name" htmlFor="name" error={fieldErrors.name}>
          <Input id="name" autoComplete="name" value={form.name} onChange={update("name")} invalid={Boolean(fieldErrors.name)} />
        </Field>
        <Field label="College email" htmlFor="email" error={fieldErrors.email} hint="Must use your campus email domain.">
          <Input
            id="email"
            type="email"
            autoComplete="email"
            placeholder="you@srmap.edu.in"
            value={form.email}
            onChange={update("email")}
            invalid={Boolean(fieldErrors.email)}
          />
        </Field>
        <Field label="Password" htmlFor="password" error={fieldErrors.password} hint="At least 8 characters.">
          <Input
            id="password"
            type="password"
            autoComplete="new-password"
            value={form.password}
            onChange={update("password")}
            invalid={Boolean(fieldErrors.password)}
          />
        </Field>
        <div className="grid gap-5 sm:grid-cols-2">
          <Field label="Branch or major" htmlFor="branch" error={fieldErrors.branch}>
            <Input id="branch" placeholder="Computer Science" value={form.branch} onChange={update("branch")} invalid={Boolean(fieldErrors.branch)} />
          </Field>
          <Field label="Year" htmlFor="year" error={fieldErrors.year}>
            <Select id="year" value={form.year} onChange={update("year")} invalid={Boolean(fieldErrors.year)}>
              {[1, 2, 3, 4, 5].map((year) => (
                <option key={year} value={year}>
                  Year {year}
                </option>
              ))}
            </Select>
          </Field>
        </div>
        <Field label="Short bio (optional)" htmlFor="bio" error={fieldErrors.bio}>
          <Textarea id="bio" placeholder="What are you studying, and what do you love to share?" value={form.bio} onChange={update("bio")} invalid={Boolean(fieldErrors.bio)} />
        </Field>
        <Button type="submit" size="lg" className="w-full" loading={submitting}>
          Create account
        </Button>
      </form>
      <p className="mt-6 text-center text-sm text-muted-foreground">
        Already a member?{" "}
        <Link to="/login" className="font-semibold text-primary-700 hover:underline dark:text-primary-300">
          Log in
        </Link>
      </p>
    </AuthShell>
  );
}
