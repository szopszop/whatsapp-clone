import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, Contact } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = '/api/v1';

  constructor(private http: HttpClient) { }

  /**
   * Get the current user's profile
   */
  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/user/me`);
  }

  /**
   * Search for users by query
   */
  searchUsers(query: string, page: number = 0, size: number = 20): Observable<any> {
    let params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.apiUrl}/user/search`, { params });
  }

  /**
   * Get all contacts for the current user
   */
  getContacts(): Observable<Contact[]> {
    return this.http.get<Contact[]>(`${this.apiUrl}/contacts`);
  }

  /**
   * Add a user as a contact
   */
  addContact(contactId: string): Observable<Contact> {
    return this.http.post<Contact>(`${this.apiUrl}/contacts`, { contactId });
  }

  /**
   * Check if a user is already a contact
   */
  isUserContact(contactId: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/contacts/check/${contactId}`);
  }

  /**
   * Get a specific contact
   */
  getContact(contactId: string): Observable<Contact> {
    return this.http.get<Contact>(`${this.apiUrl}/contacts/${contactId}`);
  }
}
