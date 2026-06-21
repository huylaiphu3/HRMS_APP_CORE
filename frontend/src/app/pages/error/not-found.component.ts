import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NzResultModule } from 'ng-zorro-antd/result';
import { NzButtonModule } from 'ng-zorro-antd/button';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [NzResultModule, NzButtonModule, RouterLink],
  template: `
    <nz-result nzStatus="404" nzTitle="404" nzSubTitle="Trang khong ton tai">
      <div nz-result-extra>
        <a nz-button nzType="primary" routerLink="/dashboard">Ve Dashboard</a>
      </div>
    </nz-result>
  `,
})
export class NotFoundComponent {}
