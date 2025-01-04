import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { enviroment } from '../enviroments/enviroment';
import { Category } from '../models/category';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiCategories = `${enviroment.apiBaseUrl}/categories`;

  constructor(private http: HttpClient) {}

  getCategories(page: number, limit: number): Observable<Category[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());
    return this.http.get<Category[]>(this.apiCategories, { params });
  }

  getCategoryById(id: number): Observable<any> {
    return this.http.get(`${this.apiCategories}/${id}`);
  }

  updateCategory(id: number, category: Category): Observable<any> {
    return this.http.put(`${this.apiCategories}/${id}`, category);
  }

  deleteCategory(id: number): Observable<any> {
    return this.http.delete(`${this.apiCategories}/${id}`, {
      responseType: 'text',
    });
  }
}
