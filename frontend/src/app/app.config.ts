import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { vi_VN, NZ_I18N } from 'ng-zorro-antd/i18n';
import { registerLocaleData } from '@angular/common';
import vi from '@angular/common/locales/vi';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { IconDefinition } from '@ant-design/icons-angular';
import {
  DashboardOutline,
  TeamOutline,
  UserOutline,
  ApartmentOutline,
  IdcardOutline,
  FileProtectOutline,
  ClockCircleOutline,
  CalendarOutline,
  DollarOutline,
  AuditOutline,
  BarChartOutline,
  SettingOutline,
  BellOutline,
  MailOutline,
  LockOutline,
  EyeOutline,
  EyeInvisibleOutline,
  LogoutOutline,
  MenuFoldOutline,
  MenuUnfoldOutline,
  DownOutline,
  CalculatorOutline,
  MoneyCollectOutline,
  GlobalOutline,
  NodeIndexOutline,
  SearchOutline,
} from '@ant-design/icons-angular/icons';
import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';

registerLocaleData(vi);

const icons: IconDefinition[] = [
  DashboardOutline, TeamOutline, UserOutline, ApartmentOutline, IdcardOutline,
  FileProtectOutline, ClockCircleOutline, CalendarOutline, DollarOutline,
  AuditOutline, BarChartOutline, SettingOutline,
  BellOutline, MailOutline, LockOutline, EyeOutline,
  EyeInvisibleOutline, LogoutOutline, MenuFoldOutline, MenuUnfoldOutline,
  DownOutline, CalculatorOutline, MoneyCollectOutline, GlobalOutline,
  NodeIndexOutline, SearchOutline,
];

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideAnimations(),
    { provide: NZ_I18N, useValue: vi_VN },
    importProvidersFrom(NzIconModule.forRoot(icons)),
  ],
};
