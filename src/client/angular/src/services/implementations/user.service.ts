import { Injectable } from "@angular/core";
import { ManagerService } from "../manager.service";
import { HttpClient } from "@angular/common/http";
import { IUserService } from "../interfaces/user-service.interface";

@Injectable()
export class UserService extends ManagerService implements IUserService {

    constructor(public override httpClient: HttpClient) {
        super('users', httpClient);
    }
}
