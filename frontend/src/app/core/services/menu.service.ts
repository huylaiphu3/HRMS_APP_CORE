import { Injectable, computed } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { MenuItem } from '../models/menu.model';

const MENU_ITEMS: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: 'dashboard',
    route: '/dashboard',
    roles: ['ADMIN', 'MANAGER', 'EMPLOYEE'],
  },
  {
    label: 'Nhân sự',
    icon: 'team',
    roles: ['ADMIN', 'MANAGER'],
    children: [
      { label: 'Nhân viên', icon: 'user', route: '/employees', roles: ['ADMIN', 'MANAGER'] },
      { label: 'Phòng ban', icon: 'apartment', route: '/departments', roles: ['ADMIN'] },
      { label: 'Chức vụ', icon: 'idcard', route: '/positions', roles: ['ADMIN'] },
    ],
  },
  {
    label: 'Hợp đồng',
    icon: 'file-protect',
    route: '/contracts',
    roles: ['ADMIN'],
  },
  {
    label: 'Chấm công',
    icon: 'clock-circle',
    route: '/attendance',
    roles: ['ADMIN', 'MANAGER', 'EMPLOYEE'],
  },
  {
    label: 'Nghỉ phép',
    icon: 'calendar',
    route: '/leave',
    roles: ['ADMIN', 'MANAGER', 'EMPLOYEE'],
  },
  {
    label: 'Tiền lương',
    icon: 'dollar',
    route: '/payroll',
    roles: ['ADMIN'],
  },
  {
    label: 'Phê duyệt',
    icon: 'audit',
    route: '/approvals',
    roles: ['ADMIN', 'MANAGER'],
  },
  {
    label: 'Báo cáo',
    icon: 'bar-chart',
    route: '/reports',
    roles: ['ADMIN'],
  },
  {
    label: 'Cấu hình',
    icon: 'setting',
    roles: ['ADMIN'],
    children: [
      { label: 'Thông số lương', icon: 'calculator', route: '/config/payroll', roles: ['ADMIN'] },
      { label: 'Phụ cấp', icon: 'money-collect', route: '/config/allowance', roles: ['ADMIN'] },
      { label: 'IP Whitelist', icon: 'global', route: '/config/ip-whitelist', roles: ['ADMIN'] },
      { label: 'Quy trình duyệt', icon: 'node-index', route: '/config/workflow', roles: ['ADMIN'] },
    ],
  },
];

@Injectable({ providedIn: 'root' })
export class MenuService {
  constructor(private auth: AuthService) {}

  readonly visibleMenu = computed(() => {
    const role = this.auth.role();
    if (!role) return [];
    return this.filterByRole(MENU_ITEMS, role);
  });

  private filterByRole(items: MenuItem[], role: string): MenuItem[] {
    return items
      .filter((item) => item.roles.includes(role as any))
      .map((item) => ({
        ...item,
        children: item.children ? this.filterByRole(item.children, role) : undefined,
      }))
      .filter((item) => !item.children || item.children.length > 0);
  }
}
