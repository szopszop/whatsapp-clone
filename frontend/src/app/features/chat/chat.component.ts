import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService } from '../../core/services/user.service';
import { ConnectionService } from '../../core/services/connection.service';
import { MessageService } from '../../core/services/message.service';
import { User } from '../../core/models/user.model';
import { Connection } from '../../core/models/connection.model';
import { Message } from '../../core/models/message.model';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatTabsModule,
    MatIconModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatCardModule,
    MatListModule,
    MatDividerModule,
    MatBadgeModule,
    MatTooltipModule,
    MatSnackBarModule
  ],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
  // User search
  searchQuery: string = '';
  searchResults: User[] = [];
  isSearching: boolean = false;

  // Contacts
  contacts: User[] = [];
  isLoadingContacts: boolean = false;

  // Connection requests
  pendingRequests: Connection[] = [];
  isLoadingRequests: boolean = false;

  // Chat
  selectedContact: User | null = null;
  messages: Message[] = [];
  newMessage: string = '';
  isLoadingMessages: boolean = false;
  currentPage: number = 0;
  totalMessages: number = 0;

  constructor(
    private userService: UserService,
    private connectionService: ConnectionService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.loadContacts();
    this.loadPendingRequests();
  }

  searchUsers(): void {
    if (!this.searchQuery.trim()) {
      this.searchResults = [];
      return;
    }

    this.isSearching = true;
    this.userService.searchUsers(this.searchQuery).subscribe({
      next: (response) => {
        this.searchResults = response.content;
        this.isSearching = false;
      },
      error: (error) => {
        console.error('Error searching users:', error);
        this.isSearching = false;
      }
    });
  }

  sendConnectionRequest(userId: string): void {
    this.connectionService.sendConnectionRequest(userId).subscribe({
      next: () => {
        this.searchResults = this.searchResults.filter(user => user.id !== userId);
      },
      error: (error) => {
        console.error('Error sending connection request:', error);
      }
    });
  }

  loadContacts(): void {
    this.isLoadingContacts = true;
    this.connectionService.getMyConnections().subscribe({
      next: (contacts) => {
        this.contacts = contacts;
        this.isLoadingContacts = false;
      },
      error: (error) => {
        console.error('Error loading contacts:', error);
        this.isLoadingContacts = false;
      }
    });
  }

  selectContact(contact: User): void {
    this.selectedContact = contact;
    this.currentPage = 0;
    this.loadMessages();
  }

  loadPendingRequests(): void {
    this.isLoadingRequests = true;
    this.connectionService.getPendingConnectionRequests().subscribe({
      next: (requests) => {
        this.pendingRequests = requests;
        this.isLoadingRequests = false;
      },
      error: (error) => {
        console.error('Error loading pending requests:', error);
        this.isLoadingRequests = false;
      }
    });
  }

  acceptConnectionRequest(requestId: string): void {
    this.connectionService.acceptConnectionRequest(requestId).subscribe({
      next: () => {
        this.pendingRequests = this.pendingRequests.filter(req => req.id !== requestId);
        this.loadContacts();
      },
      error: (error) => {
        console.error('Error accepting connection request:', error);
      }
    });
  }

  rejectConnectionRequest(requestId: string): void {
    this.connectionService.rejectConnectionRequest(requestId).subscribe({
      next: () => {
        this.pendingRequests = this.pendingRequests.filter(req => req.id !== requestId);
      },
      error: (error) => {
        console.error('Error rejecting connection request:', error);
      }
    });
  }

  // Chat methods
  loadMessages(): void {
    if (!this.selectedContact) return;

    this.isLoadingMessages = true;
    const conversationId = this.selectedContact.id; // Simplified for now

    this.messageService.getConversationMessages(conversationId, this.currentPage).subscribe({
      next: (response) => {
        this.messages = response.content;
        this.totalMessages = response.totalElements;
        this.isLoadingMessages = false;
      },
      error: (error) => {
        console.error('Error loading messages:', error);
        this.isLoadingMessages = false;
      }
    });
  }

  sendMessage(): void {
    if (!this.selectedContact || !this.newMessage.trim()) return;

    const message = {
      recipientId: this.selectedContact.id,
      content: this.newMessage.trim()
    };

    this.messageService.sendMessage(message).subscribe({
      next: (sentMessage) => {
        this.messages.push(sentMessage);
        this.newMessage = '';
      },
      error: (error) => {
        console.error('Error sending message:', error);
      }
    });
  }

  loadMoreMessages(): void {
    this.currentPage++;
    this.loadMessages();
  }
}
