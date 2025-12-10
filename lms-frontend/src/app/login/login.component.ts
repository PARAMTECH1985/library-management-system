import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { UserAuthService } from '../_service/user-auth.service';
import { UsersService } from '../_service/users.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private userService: UsersService,
    private userAuthSerivce: UserAuthService,
    private router: Router
  ) { }

  ngOnInit() {
  }

 loginData = {
  username: '',
  password: ''
};

login(form: NgForm) {
  this.userService.login(this.loginData).subscribe(
    (response: any) => {
      this.userAuthSerivce.setRoles(response.user.role);
      this.userAuthSerivce.setToken(response.jwtToken);
      this.userAuthSerivce.setUserId(response.user.userId);
      this.userAuthSerivce.setName(response.user.name);

      const role = response.user.role[0].roleName;

      if (role === 'Admin') {
        this.router.navigate(['/books']);
      } else {
        this.router.navigate(['/borrow-book']);
      }
    },
    (error) => {
      console.log("Login failed:", error);
      alert("Invalid username or password");
    }
  );
}

}