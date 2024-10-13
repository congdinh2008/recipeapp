import { CategoryViewModel } from '../category/category.view-model';
import { IngredientViewModel } from '../ingredient/ingredient.view-model';
import { RecipeIngredientViewModel } from './recipe-ingredient.view-model';

export class RecipeViewModel {
  public id!: string;
  public title!: string;
  public description!: string;
  public image!: string;
  public prepTime!: number;
  public cookTime!: number;
  public servings!: number;
  public category!: CategoryViewModel;
  public ingredients!: RecipeIngredientViewModel[];
}
