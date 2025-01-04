import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../../service/category.service';
import { Router } from 'express';
import { Category } from '../../../models/category';
import { enviroment } from '../../../enviroments/enviroment';

@Component({
  selector: 'app-category-detail',
  standalone: true,
  imports: [],
  templateUrl: './category-detail.component.html',
  styleUrl: './category-detail.component.scss',
})
export class CategoryDetailComponent implements OnInit {
  category?: Category;
  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Lấy ID từ route parameters
    this.getCategoryDetails();
  }

  getCategoryDetails(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    debugger;
    this.categoryService.getCategoryById(id).subscribe({
      next: () => {
        debugger;
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        debugger;
        console.error('Error fetching detail: ', error);
      },
    });
  }
}
