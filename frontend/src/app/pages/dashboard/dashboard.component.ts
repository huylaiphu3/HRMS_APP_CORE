import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzStatisticModule } from 'ng-zorro-antd/statistic';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NzCardModule, NzGridModule, NzStatisticModule, NzIconModule],
  template: `
    <div class="page-header">
      <h1 class="page-title">Xin chao, {{ auth.user()?.fullName }}</h1>
      <p class="page-desc">Tong quan hoat dong nhan su hom nay</p>
    </div>

    <div nz-row [nzGutter]="[20, 20]">
      <div nz-col [nzSpan]="6">
        <nz-card class="stat-card">
          <nz-statistic [nzValue]="0" nzTitle="Tong nhan vien"></nz-statistic>
        </nz-card>
      </div>
      <div nz-col [nzSpan]="6">
        <nz-card class="stat-card">
          <nz-statistic [nzValue]="0" nzTitle="Phong ban"></nz-statistic>
        </nz-card>
      </div>
      <div nz-col [nzSpan]="6">
        <nz-card class="stat-card">
          <nz-statistic [nzValue]="0" nzTitle="Don cho duyet"></nz-statistic>
        </nz-card>
      </div>
      <div nz-col [nzSpan]="6">
        <nz-card class="stat-card">
          <nz-statistic [nzValue]="0" nzTitle="Hop dong sap het han"></nz-statistic>
        </nz-card>
      </div>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 28px; }
    .page-title {
      font-size: 32px;
      font-weight: 800;
      letter-spacing: -0.04em;
      color: #111827;
      margin: 0 0 4px;
    }
    .page-desc {
      font-size: 15px;
      color: #667085;
      margin: 0;
    }
    .stat-card {
      border-radius: 20px;
      border: none;
      box-shadow: 0 18px 50px rgba(15,23,42,0.06), 0 3px 10px rgba(15,23,42,0.04);
      ::ng-deep .ant-statistic-title { font-size: 13px; font-weight: 600; color: #667085; }
      ::ng-deep .ant-statistic-content-value { font-size: 36px; font-weight: 800; letter-spacing: -0.05em; }
    }
  `],
})
export class DashboardComponent {
  constructor(public auth: AuthService) {}
}
