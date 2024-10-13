import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { USER_SERVICE } from '../../../constants/injection.constant';
import { UserService } from '../../../services/implementations/user.service';
import { UserComponent } from './user.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'user',
    component: UserComponent
  },
  {
    path: '**',
    redirectTo: 'user',
    pathMatch: 'full'
  }
]


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
  providers: [
    {
      provide: USER_SERVICE,
      useClass: UserService
    }
  ]
})
export class UserModule { }
