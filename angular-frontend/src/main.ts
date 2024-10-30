
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { HomeComponent } from './app/home/home.component';
import { DetailProductComponent } from './app/detail-product/detail-product.component';
import { OrderConfirmComponent } from './app/order-confirm/order-confirm.component';
import { RegisterComponent } from './app/register/register.component';

import { OrderComponent } from './app/order/order.component';

bootstrapApplication(
                    // HomeComponent, 
                    // DetailProductComponent,
                    // OrderConfirmComponent,
                    RegisterComponent, 
                    // OrderComponent,
                    appConfig)
  .catch((err) => console.error(err));

