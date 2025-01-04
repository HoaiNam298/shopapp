import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminComponent } from './admin.component';
import { OrderAdminComponent } from './order/order.admin.component';
import { ProductAdminComponent } from './product/product.admin.component';
import { CategoryAdminComponent } from './category/category.admin.component';
import { DetailOrderAdminComponent } from './detail-order/detail-order.admin.component';
import { AdminGuard } from '../../guards/admin.guard';
import { CategoryDetailComponent } from './category-detail/category-detail.component';

export const adminRoutes: Routes = [
  { path: 'admin', 
    component: AdminComponent,
    children: [
      {
        path: 'orders',
        component: OrderAdminComponent
      },
      {
        path: 'products',
        component: ProductAdminComponent
      },
      {
        path: 'orders/:id',
        component: DetailOrderAdminComponent
      },
      {
        path: 'categories',
        component: CategoryAdminComponent
      }
    ],
    canActivate: [AdminGuard]
   },

];
