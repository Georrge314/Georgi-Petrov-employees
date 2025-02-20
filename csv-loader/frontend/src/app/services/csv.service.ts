import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface EmployeePairs {
  employeeId1: number;
  employeeId2: number;
  projectId: number;
  days: number;
}
@Injectable({
  providedIn: 'root'
})
export class CsvService {
  private processUrl = 'http://localhost:8080/api/csv/process';

  constructor(private http: HttpClient) { }

  processFile(file: File): Observable<EmployeePairs[]> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<EmployeePairs[]>(this.processUrl, formData);
  }
}
