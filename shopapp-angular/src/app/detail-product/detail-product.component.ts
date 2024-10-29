import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-detail-product',
  standalone: true,
  imports: [HttpClientModule,
    RouterOutlet,
    FooterComponent,
    HeaderComponent
  ],
  templateUrl: './detail-product.component.html',
  styleUrl: './detail-product.component.scss'
})
export class DetailProductComponent {

}
