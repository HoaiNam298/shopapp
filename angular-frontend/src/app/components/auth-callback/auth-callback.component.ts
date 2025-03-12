import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { UserResponse } from '../../responses/user/user.response';
import { switchMap, tap } from 'rxjs';
import { AuthService } from '../../service/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { response } from 'express';
import { TokenService } from '../../service/token.service';
import { UserService } from '../../service/user.service';
import { ApiResponse } from '../../responses/api.response';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './auth-callback.component.html',
  styleUrl: './auth-callback.component.scss'
})
export class AuthCallbackComponent implements OnInit{
  userResponse?: UserResponse

  constructor(
    private router: Router,
    private authService: AuthService,
    private activatedRoute: ActivatedRoute,
    private tokenService: TokenService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    // Xác định loại đăng nhập (Google/Facebook)
    const url = this.router.url;
    let loginType: 'google' | 'facebook' | null = null;

    if (url.includes('/auth/google/callback')) {
      loginType = 'google';
    } else if (url.includes('/auth/facebook/callback')) {
      loginType = 'facebook';
    } else {
      console.error('Không xác định được nhà cung cấp xác thực!');
      return;
    }

    // Lấy mã xác thực từ URL
    this.activatedRoute.queryParams.subscribe(params => {
      const code = params['code'];
      if (!code) {
        console.error('Mã xác thực không tồn tại!');
        return;
      }

      // Gửi mã xác thực lên server để đổi lấy token
      this.authService.exchangeCodeForToken(code, loginType).pipe(
        tap((response: any) => {
          const token = response.data.token;
          if (!token) {
            console.error('Không nhận được token từ server!');
            return;
          }

          // Lưu token vào LocalStorage
          this.tokenService.setToken(token);
        }),
        switchMap(response => {
          const token = response.data.token;
          return this.userService.getUserDetails(token);
        })
      ).subscribe(
        (user) => {
          console.log('Thông tin người dùng:', user);
          // Chuyển hướng về trang chủ sau khi đăng nhập thành công
          this.router.navigate(['/home']);
        },
        (error) => {
          console.error('Lỗi xác thực:', error);
          this.router.navigate(['/login']); // Điều hướng về trang đăng nhập nếu lỗi
        }
      );
    });
  }

}
