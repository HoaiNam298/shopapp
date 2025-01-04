import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Category } from '../../../models/category';
import { CategoryService } from '../../../service/category.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  standalone: true,
  selector: 'app-category-admin',
  templateUrl: './category.admin.component.html',
  styleUrls: ['./category.admin.component.scss'],
  imports: [CommonModule, FormsModule],
})
export class CategoryAdminComponent implements OnInit {
  categories: Category[] = [];
  itemsPerPage: number = 12;
  pages: number[] = [];
  totalPages: number = 0;
  visiblePages: number[] = [];
  currentPage: number = 1;

  constructor(
    private categoryService: CategoryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getAllCategories(this.currentPage, this.itemsPerPage);
  }

  getAllCategories(page: number, limit: number) {
    this.categoryService.getCategories(page - 1, limit).subscribe({
      next: (response: any) => {
        debugger;
        this.categories = response;
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        debugger;
        console.error('Error fetching products', error);
      },
    });
  }

  onPageChange(page: number) {
    debugger;
    this.currentPage = page;
    this.getAllCategories(this.currentPage, this.itemsPerPage);
  }

  deleteCategory(id: number): void {
    // Hiển thị thông báo xác nhận xóa với SweetAlert2
    Swal.fire({
      title: 'Bạn có chắc chắn?',
      text: 'Danh mục sẽ bị xóa và không thể khôi phục!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy',
    }).then((result) => {
      if (result.isConfirmed) {
        // Gọi API xóa danh mục
        this.categoryService.deleteCategory(id).subscribe({
          next: (response: any) => {
            // Hiển thị thông báo xóa thành công
            Swal.fire({
              title: 'Xóa thành công!',
              text: 'Danh mục đã được xóa.',
              icon: 'success',
              confirmButtonText: 'OK',
            }).then(() => {
              // Điều hướng về trang danh sách danh mục sau khi xóa thành công
              this.router.navigate(['/admin/categories']);
            });
          },
          error: (error: any) => {
            // Hiển thị thông báo lỗi khi xóa thất bại
            Swal.fire({
              title: 'Lỗi!',
              text: `Đã xảy ra lỗi khi xóa danh mục: ${
                error?.error?.message || 'Vui lòng thử lại sau.'
              }`,
              icon: 'error',
              confirmButtonText: 'Thử lại',
            });
            console.error('Error deleting category: ', error);
          },
        });
      }
    });
  }
  
}
