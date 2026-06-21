import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { LoginRequest, LoginResponse, UserInfo, UserRole } from '../models/user.model';

const TOKEN_KEY = 'hrms_access_token';
const REFRESH_KEY = 'hrms_refresh_token';
const USER_KEY = 'hrms_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUser = signal<UserInfo | null>(this.loadUser());

  readonly user = this.currentUser.asReadonly();
  readonly isLoggedIn = computed(() => !!this.currentUser());
  readonly role = computed(() => this.currentUser()?.role);

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(request: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>('/api/v1/auth/login', request).pipe(
      tap((res) => {
        const { accessToken, refreshToken, user } = res.data;
        localStorage.setItem(TOKEN_KEY, accessToken);
        localStorage.setItem(REFRESH_KEY, refreshToken);
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        this.currentUser.set(user);
      }),
    );
  }

  refreshToken(): Observable<ApiResponse<LoginResponse>> {
    const refreshToken = localStorage.getItem(REFRESH_KEY);
    return this.http
      .post<ApiResponse<LoginResponse>>('/api/v1/auth/refresh', { refreshToken })
      .pipe(
        tap((res) => {
          localStorage.setItem(TOKEN_KEY, res.data.accessToken);
          localStorage.setItem(REFRESH_KEY, res.data.refreshToken);
          localStorage.setItem(USER_KEY, JSON.stringify(res.data.user));
          this.currentUser.set(res.data.user);
        }),
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  hasRole(...roles: UserRole[]): boolean {
    const currentRole = this.currentUser()?.role;
    return !!currentRole && roles.includes(currentRole);
  }

  private loadUser(): UserInfo | null {
    const stored = localStorage.getItem(USER_KEY);
    if (!stored) return null;
    try {
      return JSON.parse(stored);
    } catch {
      return null;
    }
  }
}
