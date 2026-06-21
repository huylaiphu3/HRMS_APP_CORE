import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NzResultModule } from 'ng-zorro-antd/result';
import { NzButtonModule } from 'ng-zorro-antd/button';

@Component({
  selector: 'app-forbidden',
  standalone: true,
  imports: [NzResultModule, NzButtonModule, RouterLink],
  template: `
    <nz-result nzStatus="403" nzTitle="403" nzSubTitle="Khong co quyen truy cap trang nay">
      <div nz-result-extra>
        <a nz-button nzType="primary" routerLink="/dashboard">Ve Dashboard</a>
      </div>
    </nz-result>
  `,
})
export class ForbiddenComponent {}
