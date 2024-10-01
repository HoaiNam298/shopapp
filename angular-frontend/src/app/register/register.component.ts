import { Component, ViewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { NgForm } from '@angular/forms';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterOutlet,
    FooterComponent,
    HeaderComponent,
    FormsModule,
    NgIf,
    HttpClientModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  @ViewChild('registerForm') registerForm!: NgForm;
  phone: string;
  password: string;
  retypePassword: string;
  fullName: string;
  dateOfBirth: Date;
  address: string;
  isAccepted: boolean;

  constructor( private router: Router, private userService: UserService){
    this.phone='';
    this.password='';
    this.retypePassword='';
    this.fullName='';
    this.dateOfBirth=new Date();
    this.dateOfBirth.setFullYear(this.dateOfBirth.getFullYear() - 18);
    this.address='';
    this.isAccepted=false;
  }

  onPhoneChange(){
    console.log(`Phone typed: ${this.phone}`)
  }

  onDateOfBirthChange(){

  }

  register(){
    const message = `phone: ${this.phone}` +
                    `password: ${this.password}` +
                    `retypePassword: ${this.retypePassword}` +
                    `fullName: ${this.fullName}` +
                    `address: ${this.address}` +
                    `dateOfBirth: ${this.dateOfBirth}` +
                    `isAccepted: ${this.isAccepted}`;
    // alert(message);
    
    const registerData = {
      "fullname": this.fullName,
      "phone_number": this.phone,
      "address": this.address,
      "password": this.password,
      "retype_password": this.retypePassword,
      "date_of_birth": this.dateOfBirth,
      "facebook_account_id": 0,
      "google_account_id": 0,
      "role_id": 1
    }
    
    

    this.userService.register(registerData).subscribe({
      next: (response: any) => {
        debugger
        //Xử lý kết quả khi trả về đăng ký thành công
        //Đăng ký thành công, chuyển sang màn hình login
        this.router.navigate(['/login']);
      },
      complete() {
        debugger
      },
      error(error: any) {
        //Xử lý lỗi nếu có
        alert(`Cannot register, error: ${error.error}`);
        debugger
        console.error('Đăng ký không thành công: ', error);
      },
    });
  }
  
  checkPasswordsMatch(){
    if(this.password !== this.retypePassword) {
      this.registerForm.form.controls['retypePassword']?.setErrors({'paswordMismatch': true});
    } else {
      this.registerForm.form.controls['retypePassword']?.setErrors(null);
    }
  }

  checkAge() {
    if(this.dateOfBirth) {
      const today = new Date();
      const birthDate = new Date(this.dateOfBirth);
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      if(monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }

      if(age < 18) {
        this.registerForm.form.controls['dateOfBirth'].setErrors({'invalidAge': true});
      } else {
        this.registerForm.form.controls['dateOfBirth'].setErrors(null);
      }
    }
  }
}
