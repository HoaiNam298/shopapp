import { Component, ViewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterOutlet,
    FooterComponent,
    HeaderComponent,
    FormsModule,
    NgIf
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

  constructor(){
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
                    `retype password: ${this.retypePassword}` +
                    `fullName: ${this.fullName}` +
                    `address: ${this.address}` +
                    `dateOfBirth: ${this.dateOfBirth}` +
                    `isAccepted: ${this.isAccepted}`;
    alert(message)
  }
  checkPasswordsMatch(){
    if(this.password !== this.retypePassword) {
      this.registerForm.form.controls['retypePassword'].setErrors({'paswordMismatch': true});
    } else {
      this.registerForm.form.controls['retypePassword'].setErrors(null);
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
