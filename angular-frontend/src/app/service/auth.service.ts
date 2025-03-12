import { Inject, Injectable } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { enviroment } from '../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
// import { LocalStorageService } from 'ngx-webstorage'

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private apiBaseUrl = enviroment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  authenticate(loginType: 'facebook' | 'google'): Observable<string> {
    debugger
    return this.http.get(
        `${this.apiBaseUrl}/users/auth/social-login?login_type=${loginType}`,
        { responseType: 'text' }
    );
  }

  exchangeCodeForToken(code: string, loginType: 'google' | 'facebook'): Observable<any> {
    const params = new HttpParams()
      .set('code', code)
      .set('login_type', loginType);
    
    return this.http.get<any>(`${this.apiBaseUrl}/users/auth/social/callback`, { params });
  }
}
