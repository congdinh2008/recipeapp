import { IManagerService } from './manager-service.interface';
import { BaseService } from './base.service';
import { Observable } from 'rxjs';
import { FilterModel } from '../models/filter.model';
import { HttpClient } from '@angular/common/http';

export class ManagerService extends BaseService implements IManagerService {

  constructor(protected override apiUrl: String, protected httpClient: HttpClient) {
    super(apiUrl);
  }

  getAll(): Observable<any> {
    return this.httpClient.get<any[]>(this.baseUrl);
  }
  
  search(param: FilterModel): Observable<any> {
    return this.httpClient.post<any>(this.baseUrl + '/search', param);
  }
  
  getById(id: string): Observable<any> {
    return this.httpClient.get<any>(`${this.baseUrl}/${id}`);
  }

  create(data: any): Observable<any> {
    return this.httpClient.post<any>(this.baseUrl, data);
  }
  
  update(data: any): Observable<any> {
    return this.httpClient.put<any>(`${this.baseUrl}/${data.id}`, data);
  }

  delete(id: string): Observable<any> {
    return this.httpClient.delete<any>(`${this.baseUrl}/${id}`);
  }
}
