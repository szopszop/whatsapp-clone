import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private apiUrl = '/api/v1/messages';

  constructor(private http: HttpClient) { }

  /**
   * Get messages for a conversation
   */
  getMessagesForConversation(conversationId: string, page: number = 0, size: number = 50): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.apiUrl}/conversation/${conversationId}`, { params });
  }

  /**
   * Send a message
   */
  sendMessage(conversationId: string, recipientId: string, content: string): Observable<Message> {
    return this.http.post<Message>(this.apiUrl, {
      conversationId,
      recipientId,
      content
    });
  }
}
