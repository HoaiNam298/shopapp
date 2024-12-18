import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../service/product.service';
import { CartService } from '../../service/cart.service';
import { Product } from '../../models/product';
import { ProductImage } from '../../models/product.image';
import { enviroment } from '../../enviroments/enviroment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-detail-product',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HeaderComponent,
    FooterComponent
  ],
  templateUrl: './detail-product.component.html',
  styleUrl: './detail-product.component.scss'
})
export class DetailProductComponent implements OnInit{
  product?: Product;
  productId: number = 0;
  currentImageIndex: number = 0;
  quantity: number = 1;
  isPressedAddToCart: boolean = false;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private router: Router,
    private activatedRouter: ActivatedRoute
  ) {}

  ngOnInit() {
    // Lấy productId từ URL
    this.activatedRouter.paramMap.subscribe((paramMap) => {
      const idParam = paramMap.get('id'); // Lấy tham số 'id'
      if (idParam !== null) {
        this.productId = +idParam;
      }

      // Nếu productId hợp lệ, gọi API lấy chi tiết sản phẩm
      if (!isNaN(this.productId)) {
        this.productService.getDetailProduct(this.productId).subscribe({
          next: (response: any) => {
            // Lấy danh sách ảnh sản phẩm và thay đổi URL
            if (response.product_images && response.product_images.length > 0) {
              response.product_images.forEach((product_image: ProductImage) => {
                product_image.image_url = `${enviroment.apiBaseUrl}/products/images/${product_image.image_url}`;
              });
            }
            this.product = response;

            // Hiển thị ảnh đầu tiên
            this.showImage(0);
          },
          complete: () => {
            console.log('Product details loaded successfully.');
          },
          error: (error: any) => {
            console.error('Error fetching detail:', error);
          }
        });
      } else {
        console.error('Invalid productId:', idParam);
        this.router.navigate(['/not-found']);
      }
    });
  }

  showImage(index: number) {
    debugger
    if(this.product && this.product.product_images && this.product.product_images.length > 0) {
      //đảm bảo index nằm trong khoảng hợp lệ
      if(index < 0) {
        index = 0
      } else if (index >= this.product.product_images.length) {
        index = this.product.product_images.length - 1;
      }
      //Gán index hiện tại và cập nhật ảnh hiển thị
      this.currentImageIndex = index;
    }
  }

  thumbnailClick(index: number) {
    debugger
    //Gọi khi mội thumbnail được bấm
    this.currentImageIndex = index; //Cập nhật currentImageIndex
  }

  nextImage(): void {
    debugger
    this.showImage(this.currentImageIndex + 1);
  }

  preciousImage(): void {
    debugger
    this.showImage(this.currentImageIndex - 1);
  }

  addToCard(): void {
    debugger
    if(this.product) {
      this.cartService.addToCard(this.product.id, this.quantity);
    } else {
      console.error('Không thể thêm sản phẩm vào giỏ hàng vì product là null!');
    }
  }

  increaseQuantity(): void{
    this.quantity++;
  }

  decreaseQuantity(): void{
    if(this.quantity > 1) {
      this.quantity--;
    }
  }

  getTotalPrice(): number {
    if (this.product) {
      return this.product.price * this.quantity
    }
    return 0;
  }

  buyNow(): void{
    if(this.isPressedAddToCart == false) {
      this.addToCard();
    }
    this.router.navigate(['/orders']);
  }

}
