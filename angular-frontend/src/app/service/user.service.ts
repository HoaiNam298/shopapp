import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private apiUrl = "http://localhost:6969/api/v1/users/register";

  constructor(private http: HttpClient) { }
  register(registerData: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
      //'Access-Control-Allow-Origin' : "*"
    });
    return this.http.post(this.apiUrl, registerData, {headers: headers})
  }
}