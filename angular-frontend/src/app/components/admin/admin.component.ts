import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserResponse } from '../../responses/user/user.response';
import { UserService } from '../../service/user.service';
import { TokenService } from '../../service/token.service';

@Component({
  standalone: true,
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  adminComponent: string = 'orders';
  userResponse?: UserResponse | null;

  constructor(
    private userService: UserService,
    private tokenService: TokenService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.userResponse = this.userService.getUserResponseFromLocalStorage();
    // this.route.queryParams.subscribe(params => {
    //   this.adminComponent = params['component'] || 'orders'; // 'orders' là mặc định nếu không có tham số
    // });
  }

  logout() {
    this.userService.removeUserFromLocalStorage();
    this.tokenService.removeToken();
    this.userResponse = this.userService.getUserResponseFromLocalStorage();
    this.router.navigate(['/login'])
  }

  showAdminComponent(componentName: string): void {
    // this.adminComponent = componentName;
    // this.router.navigate([], {
    //   relativeTo: this.route,
    //   queryParams: { component: componentName },
    //   queryParamsHandling: 'merge', // Giữ lại các query params khác (nếu có)
    // });

    if(componentName == 'orders') {
      this.router.navigate(['/admin/orders'])
    } else if(componentName == 'categories') {
      this.router.navigate(['/admin/categories'])
    } else if(componentName == 'products') {
      this.router.navigate(['/admin/products'])
    }
  }
}
