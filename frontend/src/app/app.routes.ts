import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./pages/login/login.component').then((m) => m.LoginComponent),
      },
    ],
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'employees',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
        loadComponent: () =>
          import('./pages/employee/employee-list.component').then((m) => m.EmployeeListComponent),
      },
      {
        path: 'departments',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/department/department-list.component').then((m) => m.DepartmentListComponent),
      },
      {
        path: 'positions',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/position/position-list.component').then((m) => m.PositionListComponent),
      },
      {
        path: 'contracts',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/contract/contract-list.component').then((m) => m.ContractListComponent),
      },
      {
        path: 'attendance',
        loadComponent: () =>
          import('./pages/attendance/attendance.component').then((m) => m.AttendanceComponent),
      },
      {
        path: 'leave',
        loadComponent: () =>
          import('./pages/leave/leave.component').then((m) => m.LeaveComponent),
      },
      {
        path: 'payroll',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/payroll/payroll.component').then((m) => m.PayrollComponent),
      },
      {
        path: 'approvals',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] },
        loadComponent: () =>
          import('./pages/approval/approval.component').then((m) => m.ApprovalComponent),
      },
      {
        path: 'reports',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () =>
          import('./pages/report/report.component').then((m) => m.ReportComponent),
      },
      {
        path: 'config',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        children: [
          {
            path: 'payroll',
            loadComponent: () =>
              import('./pages/config/config-payroll.component').then((m) => m.ConfigPayrollComponent),
          },
          {
            path: 'allowance',
            loadComponent: () =>
              import('./pages/config/config-allowance.component').then((m) => m.ConfigAllowanceComponent),
          },
          {
            path: 'ip-whitelist',
            loadComponent: () =>
              import('./pages/config/config-ip-whitelist.component').then((m) => m.ConfigIpWhitelistComponent),
          },
          {
            path: 'workflow',
            loadComponent: () =>
              import('./pages/config/config-workflow.component').then((m) => m.ConfigWorkflowComponent),
          },
        ],
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/profile/profile.component').then((m) => m.ProfileComponent),
      },
    ],
  },
  {
    path: '403',
    loadComponent: () =>
      import('./pages/error/forbidden.component').then((m) => m.ForbiddenComponent),
  },
  {
    path: '**',
    loadComponent: () =>
      import('./pages/error/not-found.component').then((m) => m.NotFoundComponent),
  },
];
