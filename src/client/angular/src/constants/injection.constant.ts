import { InjectionToken } from "@angular/core";
import { ICategoryService } from "../services/interfaces/category-service.interface";

export const CATEGORY_SERVICE = new InjectionToken<ICategoryService>(
  'CATEGORY_SERVICE'
);