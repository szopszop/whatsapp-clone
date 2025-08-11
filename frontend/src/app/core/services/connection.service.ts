import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Connection } from '../models/connection.model';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ConnectionService {
  private apiUrl = `${environment.gatewayApiUrl}/api/v1/connections`;

  constructor(private http: HttpClient) { }

  /**
   * Get all connections for the current user
   */
  getMyConnections(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  /**
   * Get all pending connection requests for the current user
   */
  getPendingConnectionRequests(): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.apiUrl}/pending`);
  }

  /**
   * Send a connection request to another user
   */
  sendConnectionRequest(targetUserId: string): Observable<Connection> {
    return this.http.post<Connection>(`${this.apiUrl}/request`, { targetUserId });
  }

  /**
   * Accept a connection request
   */
  acceptConnectionRequest(requestId: string): Observable<Connection> {
    return this.http.post<Connection>(`${this.apiUrl}/accept/${requestId}`, {});
  }

  /**
   * Reject a connection request
   */
  rejectConnectionRequest(requestId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reject/${requestId}`, {});
  }

  /**
   * Remove a connection
   */
  removeConnection(connectionId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${connectionId}`);
  }
}
