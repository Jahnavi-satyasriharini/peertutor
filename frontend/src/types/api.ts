export type Role = "STUDENT" | "ADMIN";
export type SkillType = "TEACH" | "LEARN";
export type RequestStatus = "PENDING" | "ACCEPTED" | "REJECTED" | "COMPLETED";
export type StudyMode = "ONLINE" | "OFFLINE";

export interface User {
  id: number;
  name: string;
  email: string;
  branch: string | null;
  year: number | null;
  bio: string | null;
  role: Role;
}

export interface Skill {
  id: number;
  name: string;
  category: string;
}

export interface UserSkill {
  id: number;
  skillId: number;
  name: string;
  category: string;
}

export interface Profile {
  id: number;
  name: string;
  email: string;
  branch: string | null;
  year: number | null;
  bio: string | null;
  role: Role;
  teachSkills: UserSkill[];
  learnSkills: UserSkill[];
  averageRating: number | null;
  ratingCount: number;
}

export interface PersonSummary {
  id: number;
  name: string;
  branch: string | null;
  year: number | null;
}

export interface Tutor {
  id: number;
  name: string;
  branch: string | null;
  year: number | null;
  bio: string | null;
  teachSkills: Skill[];
  averageRating: number | null;
  ratingCount: number;
}

export interface SwapRequest {
  id: number;
  requester: PersonSummary;
  receiver: PersonSummary;
  skill: Skill;
  status: RequestStatus;
  date: string;
  rating: number | null;
}

export interface RequestStats {
  pending: number;
  accepted: number;
  rejected: number;
  completed: number;
}

export interface ActivityItem {
  requestId: number;
  message: string;
  status: RequestStatus;
  date: string;
}

export interface Dashboard {
  name: string;
  teachSkills: UserSkill[];
  learnSkills: UserSkill[];
  pendingRequests: number;
  completedExchanges: number;
  activeGroups: number;
  requestStats: RequestStats;
  recentActivity: ActivityItem[];
}

export interface GroupSummary {
  id: number;
  name: string;
  subject: string;
  description: string | null;
  maxMembers: number | null;
  memberCount: number;
  mode: StudyMode;
  creator: PersonSummary;
  joined: boolean;
  owner: boolean;
  full: boolean;
}

export interface GroupDetail {
  group: GroupSummary;
  members: PersonSummary[];
}

export interface PreviewTutor {
  name: string;
  branch: string | null;
  year: number | null;
  teachSkills: Skill[];
  averageRating: number | null;
  ratingCount: number;
}

export interface PreviewGroup {
  id: number;
  name: string;
  subject: string;
  mode: StudyMode;
  memberCount: number;
  maxMembers: number | null;
}

export interface PublicPreview {
  tutors: PreviewTutor[];
  groups: PreviewGroup[];
  skills: Skill[];
}

export interface AdminStats {
  totalUsers: number;
  totalGroups: number;
  totalSwapRequests: number;
}

export interface SignupInput {
  name: string;
  email: string;
  password: string;
  branch: string;
  year: number;
  bio?: string;
}

export interface ProfileInput {
  name: string;
  branch: string;
  year: number;
  bio?: string;
}

export interface GroupInput {
  name: string;
  subject: string;
  description?: string;
  maxMembers: number;
  mode: StudyMode;
}
