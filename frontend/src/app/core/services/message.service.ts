import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Message, SendMessageRequest } from '../models/message.model';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private apiUrl = `${environment.gatewayApiUrl}/api/v1/messages`;

  constructor(private http: HttpClient) { }

  /**
   * Send a message to another user
   */
  sendMessage(message: SendMessageRequest): Observable<Message> {
    return this.http.post<Message>(this.apiUrl, message);
  }

  /**
   * Get messages for a conversation
   */
  getConversationMessages(conversationId: string, page: number = 0, size: number = 50): Observable<{ content: Message[], totalElements: number }> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<{ content: Message[], totalElements: number }>(`${this.apiUrl}/conversation/${conversationId}`, { params });
  }

  /**
   * Mark a message as read
   */
  markMessageAsRead(messageId: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${messageId}/read`, {});
  }

  /**
   * Get unread message count
   */
  getUnreadMessageCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread/count`);
  }
}
