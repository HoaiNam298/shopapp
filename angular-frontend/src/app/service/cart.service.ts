import { Inject, Injectable } from '@angular/core';
import { ProductService } from './product.service';
import { DOCUMENT } from '@angular/common';
// import { LocalStorageService } from 'ngx-webstorage'

@Injectable({
  providedIn: 'root',
})
export class CartService {

  private cart: Map<number, number> = new Map();
  localStorage?: Storage;

  constructor(
    @Inject(DOCUMENT) private document: Document
  ) { 
    //Lấy dữ liệu giỏ hành từ localStorage khi khởi tạo service
    this.localStorage = document.defaultView?.localStorage
    
  }

  public refreshCart(){
    const storedCard = this.localStorage?.getItem(this.getCartKey());
    if(storedCard) {
        this.cart = new Map(JSON.parse(storedCard));
    } else {
      this.cart = new Map<number, number>();
    }
  }

  getCartKey(): string {
    const userResponseJSON = this.localStorage?.getItem('user');
    const userResponse = JSON.parse(userResponseJSON!);
    debugger;
    return `cart: ${userResponse.id}`;
  }

  addToCard(productId: number, quantity: number): void {
    debugger
    if(this.cart.has(productId)) {
        //Nếu sp đã có trong giỏ hàng, tăng số lượng lên
        this.cart.set(productId, this.cart.get(productId)! + quantity);
    } else {
        //Nếu sp chưa có trong giỏ hành, thêm sp vào với quantity
        this.cart.set(productId, quantity);
    }

    //Sau khi thay đổi giỏ hàng, lưu trữ nó vào localStorage
    this.saveCartToLocalStorage();
  }

  getCart(): Map<number, number> {
    return this.cart;
  }

  //Lưu trữ giỏ hành vào localStorage
  saveCartToLocalStorage(): void {
    debugger
    this.localStorage?.setItem(this.getCartKey(), JSON.stringify(Array.from(this.cart.entries())));
  }

  setCart(cart: Map<number, number>) {
    this.cart = cart ?? new Map<number, number>();
    this.saveCartToLocalStorage();
  }

  clearCart(): void {
    this.cart.clear();
    this.saveCartToLocalStorage();
  }
}
