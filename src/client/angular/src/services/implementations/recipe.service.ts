import { Injectable } from "@angular/core";
import { ManagerService } from "../manager.service";
import { HttpClient } from "@angular/common/http";
import { IRecipeService } from "../interfaces/recipe-service.interface";

@Injectable()
export class RecipeService extends ManagerService implements IRecipeService {

    constructor(public override httpClient: HttpClient) {
        super('recipes', httpClient);
    }
}
