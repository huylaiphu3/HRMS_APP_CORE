import { UserRole } from './user.model';

export interface MenuItem {
  label: string;
  icon: string;
  route?: string;
  roles: UserRole[];
  children?: MenuItem[];
  badge?: number;
}
