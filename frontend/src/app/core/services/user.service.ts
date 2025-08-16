import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.gatewayApiUrl}/api/v1/user`;

  constructor(private http: HttpClient) { }

  /**
   * Get the current user's profile
   */
  getMyProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  /**
   * Update the current user's profile
   */
  updateMyProfile(profileData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me`, profileData);
  }

  /**
   * Update the current user's status
   */
  updateMyStatus(status: { status: string }): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/me/status`, status);
  }

  /**
   * Search for users by query string
   */
  searchUsers(query: string, page: number = 0, size: number = 20): Observable<{ content: User[], totalElements: number }> {
    let params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<{ content: User[], totalElements: number }>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Get a user by ID
   */
  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/id?id=${id}`);
  }

  /**
   * Get a user by email
   */
  getUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/by-email?email=${email}`);
  }

  /**
   * Check if an email exists
   */
  checkEmailExists(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/exists-by-email?email=${email}`);
  }

  /**
   * Add FCM token for push notifications
   */
  addFcmToken(token: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/me/fcm-token`, token);
  }
}
