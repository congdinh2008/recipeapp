import { Injectable } from "@angular/core";
import { ManagerService } from "../manager.service";
import { HttpClient } from "@angular/common/http";
import { IRoleService } from "../interfaces/role-service.interface";

@Injectable()
export class RoleService extends ManagerService implements IRoleService {

    constructor(public override httpClient: HttpClient) {
        super('roles', httpClient);
    }
}
