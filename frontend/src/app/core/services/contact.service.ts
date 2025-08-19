import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Contact } from '../models/contact.model';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private apiUrl = `${environment.gatewayApiUrl}/api/v1/users/contacts`;

  constructor(private http: HttpClient) { }

  /**
   * Get all contacts for the current user
   */
  getMyContacts(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  /**
   * Get all pending contact requests for the current user
   */
  getPendingContactRequests(): Observable<Contact[]> {
    return this.http.get<Contact[]>(`${this.apiUrl}/pending`);
  }

  /**
   * Send a contact request to another user
   */
  sendContactRequest(targetUserId: string): Observable<Contact> {
    return this.http.post<Contact>(`${this.apiUrl}/request`, { targetUserId });
  }

  /**
   * Accept a contact request
   */
  acceptContactRequest(requestId: string): Observable<Contact> {
    return this.http.post<Contact>(`${this.apiUrl}/accept/${requestId}`, {});
  }

  /**
   * Reject a contact request
   */
  rejectContactRequest(requestId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reject/${requestId}`, {});
  }

  /**
   * Remove a contact
   */
  removeContact(contactId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${contactId}`);
  }
}
