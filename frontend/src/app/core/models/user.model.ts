export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: UserInfo;
}

export interface UserInfo {
  id: number;
  email: string;
  fullName: string;
  role: UserRole;
  departmentId?: number;
  departmentName?: string;
  avatar?: string;
}

export type UserRole = 'ADMIN' | 'MANAGER' | 'EMPLOYEE';
