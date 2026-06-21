import { Component } from '@angular/core';
import { NzEmptyModule } from 'ng-zorro-antd/empty';

@Component({
  selector: 'app-leave',
  standalone: true,
  imports: [NzEmptyModule],
  template: `
    <h1 style="font-size:32px;font-weight:800;letter-spacing:-0.04em;margin-bottom:20px">Nghi phep</h1>
    <nz-empty nzNotFoundContent="Chuc nang dang phat trien"></nz-empty>
  `,
})
export class LeaveComponent {}
