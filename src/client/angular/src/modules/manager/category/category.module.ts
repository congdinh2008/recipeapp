import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CATEGORY_SERVICE } from '../../../constants/injection.constant';
import { CategoryService } from '../../../services/implementations/category.service';
import { CategoryComponent } from './category.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'category',
    component: CategoryComponent
  },
  {
    path: '**',
    redirectTo: 'category',
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
      provide: CATEGORY_SERVICE,
      useClass: CategoryService
    }
  ]
})
export class CategoryModule { }
