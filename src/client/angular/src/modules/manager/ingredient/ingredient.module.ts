import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { INGREDIENT_SERVICE } from '../../../constants/injection.constant';
import { IngredientService } from '../../../services/implementations/ingredient.service';
import { IngredientComponent } from './ingredient.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'ingredient',
    component: IngredientComponent
  },
  {
    path: '**',
    redirectTo: 'ingredient',
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
      provide: INGREDIENT_SERVICE,
      useClass: IngredientService
    }
  ]
})
export class IngredientModule { }
