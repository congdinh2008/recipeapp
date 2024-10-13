import { InjectionToken } from '@angular/core';
import { ICategoryService } from '../services/interfaces/category-service.interface';
import { IIngredientService } from '../services/interfaces/ingredient-service.interface';

export const CATEGORY_SERVICE = new InjectionToken<ICategoryService>(
  'CATEGORY_SERVICE'
);

export const INGREDIENT_SERVICE = new InjectionToken<IIngredientService>(
  'INGREDIENT_SERVICE'
);
