import { Observable } from "rxjs";

export interface IManagerService {
    getAll(): Observable<any>;

    search(param: any): Observable<any>;

    getById(id: string): Observable<any>;

    create(data: any): Observable<any>;

    update(data: any): Observable<any>;

    delete(id: string): Observable<any>;
}
