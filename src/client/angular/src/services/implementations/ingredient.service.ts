import { Injectable } from '@angular/core';
import { ManagerService } from '../manager.service';
import { HttpClient } from '@angular/common/http';
import { IIngredientService } from '../interfaces/ingredient-service.interface';

@Injectable()
export class IngredientService
  extends ManagerService
  implements IIngredientService
{
  constructor(public override httpClient: HttpClient) {
    super('ingredients', httpClient);
  }
}
