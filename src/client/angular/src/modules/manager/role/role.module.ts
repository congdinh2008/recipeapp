import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ROLE_SERVICE } from '../../../constants/injection.constant';
import { RoleService } from '../../../services/implementations/role.service';
import { RoleComponent } from './role.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'role',
    component: RoleComponent
  },
  {
    path: '**',
    redirectTo: 'role',
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
      provide: ROLE_SERVICE,
      useClass: RoleService
    }
  ]
})
export class RoleModule { }
