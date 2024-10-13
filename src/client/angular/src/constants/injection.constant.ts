import { InjectionToken } from '@angular/core';
import { ICategoryService } from '../services/interfaces/category-service.interface';
import { IIngredientService } from '../services/interfaces/ingredient-service.interface';
import { IRoleService } from '../services/interfaces/role-service.interface';
import { IUserService } from '../services/interfaces/user-service.interface';
import { IRecipeService } from '../services/interfaces/recipe-service.interface';

export const CATEGORY_SERVICE = new InjectionToken<ICategoryService>(
  'CATEGORY_SERVICE'
);

export const INGREDIENT_SERVICE = new InjectionToken<IIngredientService>(
  'INGREDIENT_SERVICE'
);

export const ROLE_SERVICE = new InjectionToken<IRoleService>('ROLE_SERVICE');

export const USER_SERVICE = new InjectionToken<IUserService>('USER_SERVICE');

export const RECIPE_SERVICE = new InjectionToken<IRecipeService>(
  'RECIPE_SERVICE'
);
