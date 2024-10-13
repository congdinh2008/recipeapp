import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BaseService {
  protected baseUrl: string = 'http://localhost:8080/api/';

  constructor(protected apiUrl: String) {
    this.baseUrl = this.baseUrl + apiUrl;
  }
}
