import { request } from "./http";
import type {
  AdminStats,
  Dashboard,
  GroupDetail,
  GroupInput,
  GroupSummary,
  Profile,
  ProfileInput,
  PublicPreview,
  SignupInput,
  Skill,
  RequestStatus,
  SkillType,
  StudyMode,
  SwapRequest,
  Tutor,
  User,
} from "../types/api";

export const authApi = {
  me: () => request<User>("/api/auth/me"),
  login: (email: string, password: string) =>
    request<User>("/api/auth/login", { method: "POST", body: { email, password } }),
  signup: (input: SignupInput) => request<User>("/api/auth/signup", { method: "POST", body: input }),
  logout: () => request<void>("/api/auth/logout", { method: "POST" }),
};

export const profileApi = {
  get: () => request<Profile>("/api/users/me"),
  update: (input: ProfileInput) => request<Profile>("/api/users/me", { method: "PUT", body: input }),
  addSkill: (skillId: number, type: SkillType) =>
    request<Profile>("/api/users/me/skills", { method: "POST", body: { skillId, type } }),
  removeSkill: (userSkillId: number) =>
    request<Profile>(`/api/users/me/skills/${userSkillId}`, { method: "DELETE" }),
};

export const skillsApi = {
  list: () => request<Skill[]>("/api/skills"),
};

export interface TutorFilters {
  skill?: string;
  branch?: string;
  year?: number;
}

export const tutorsApi = {
  search: (filters: TutorFilters) => {
    const params = new URLSearchParams();
    if (filters.skill) params.set("skill", filters.skill);
    if (filters.branch) params.set("branch", filters.branch);
    if (filters.year) params.set("year", String(filters.year));
    const query = params.toString();
    return request<Tutor[]>(`/api/tutors${query ? `?${query}` : ""}`);
  },
};

export const requestsApi = {
  incoming: () => request<SwapRequest[]>("/api/requests/incoming"),
  outgoing: () => request<SwapRequest[]>("/api/requests/outgoing"),
  create: (receiverId: number, skillId: number) =>
    request<SwapRequest>("/api/requests", { method: "POST", body: { receiverId, skillId } }),
  updateStatus: (id: number, status: RequestStatus) =>
    request<SwapRequest>(`/api/requests/${id}/status`, { method: "POST", body: { status } }),
  rate: (id: number, rating: number) =>
    request<SwapRequest>(`/api/requests/${id}/rating`, { method: "POST", body: { rating } }),
};

export interface GroupFilters {
  q?: string;
  mode?: StudyMode;
}

export const groupsApi = {
  list: (filters: GroupFilters) => {
    const params = new URLSearchParams();
    if (filters.q) params.set("q", filters.q);
    if (filters.mode) params.set("mode", filters.mode);
    const query = params.toString();
    return request<GroupSummary[]>(`/api/groups${query ? `?${query}` : ""}`);
  },
  get: (id: number) => request<GroupDetail>(`/api/groups/${id}`),
  create: (input: GroupInput) => request<GroupSummary>("/api/groups", { method: "POST", body: input }),
  join: (id: number) => request<GroupDetail>(`/api/groups/${id}/join`, { method: "POST" }),
  leave: (id: number) => request<GroupDetail>(`/api/groups/${id}/leave`, { method: "POST" }),
};

export const dashboardApi = {
  get: () => request<Dashboard>("/api/dashboard"),
};

export const adminApi = {
  stats: () => request<AdminStats>("/api/admin/stats"),
  users: (q: string) => {
    const query = q ? `?q=${encodeURIComponent(q)}` : "";
    return request<User[]>(`/api/admin/users${query}`);
  },
  deleteUser: (id: number) => request<void>(`/api/admin/users/${id}`, { method: "DELETE" }),
};

export const publicApi = {
  preview: () => request<PublicPreview>("/api/public/preview"),
};
