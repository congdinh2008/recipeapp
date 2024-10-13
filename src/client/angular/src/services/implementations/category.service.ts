import { Injectable } from "@angular/core";
import { ICategoryService } from "../interfaces/category-service.interface";
import { ManagerService } from "../manager.service";
import { HttpClient } from "@angular/common/http";

@Injectable()
export class CategoryService extends ManagerService implements ICategoryService {

    constructor(public override httpClient: HttpClient) {
        super('categories', httpClient);
    }
}