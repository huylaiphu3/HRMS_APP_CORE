import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="auth-layout">
      <div class="auth-left">
        <div class="auth-brand">
          <div class="auth-logo">H</div>
          <h1>HRMS</h1>
          <p>People Operations</p>
        </div>
      </div>
      <div class="auth-right">
        <router-outlet />
      </div>
    </div>
  `,
  styles: [`
    .auth-layout {
      display: flex;
      min-height: 100vh;
    }
    .auth-left {
      flex: 1;
      background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      overflow: hidden;
      &::before {
        content: '';
        position: absolute;
        width: 500px;
        height: 500px;
        background: radial-gradient(circle, rgba(22,119,255,0.15), transparent 70%);
        top: -100px;
        right: -100px;
      }
    }
    .auth-brand {
      text-align: center;
      position: relative;
      z-index: 1;
      h1 { color: #fff; font-size: 48px; font-weight: 800; letter-spacing: -0.04em; margin: 16px 0 4px; }
      p { color: #94a3b8; font-size: 16px; font-weight: 500; }
    }
    .auth-logo {
      width: 80px;
      height: 80px;
      background: linear-gradient(135deg, #1677ff, #69b1ff);
      border-radius: 20px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-weight: 800;
      font-size: 40px;
      box-shadow: 0 20px 60px rgba(22,119,255,0.3);
    }
    .auth-right {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px;
      background: #f6f8fb;
    }
  `],
})
export class AuthLayoutComponent {}
