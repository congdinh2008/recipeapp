import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CATEGORY_SERVICE, RECIPE_SERVICE } from '../../../constants/injection.constant';
import { RecipeService } from '../../../services/implementations/recipe.service';
import { RecipeComponent } from './recipe.component';
import { RouterModule, Routes } from '@angular/router';
import { CategoryService } from '../../../services/implementations/category.service';

const routes: Routes = [
  {
    path: 'recipe',
    component: RecipeComponent
  },
  {
    path: '**',
    redirectTo: 'recipe',
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
      provide: RECIPE_SERVICE,
      useClass: RecipeService
    },
    {
      provide: CATEGORY_SERVICE,
      useClass: CategoryService
    }
  ]
})
export class RecipeModule { }
