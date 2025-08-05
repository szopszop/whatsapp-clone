import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { MessageService } from '../../core/services/message.service';
import { User, Contact, Message } from '../../core/models/user.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
  @ViewChild('messagesContainer') private messagesContainer: ElementRef;

  // User data
  currentUser: User | null = null;
  contacts: Contact[] = [];
  selectedContact: Contact | null = null;

  // Search
  searchQuery: string = '';
  searchResults: User[] = [];
  isSearching: boolean = false;

  // Messages
  messages: Message[] = [];
  newMessage: string = '';
  isLoadingMessages: boolean = false;
  currentPage: number = 0;
  totalPages: number = 0;

  constructor(
    private userService: UserService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadContacts();
  }

  // User methods
  loadCurrentUser(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
      },
      error: (error) => {
        console.error('Error loading current user:', error);
      }
    });
  }

  // Contact methods
  loadContacts(): void {
    this.userService.getContacts().subscribe({
      next: (contacts) => {
        this.contacts = contacts;
      },
      error: (error) => {
        console.error('Error loading contacts:', error);
      }
    });
  }

  selectContact(contact: Contact): void {
    this.selectedContact = contact;
    this.loadMessages(contact.conversationId);
  }

  // Search methods
  searchUsers(): void {
    if (!this.searchQuery.trim()) {
      this.searchResults = [];
      return;
    }

    this.isSearching = true;
    this.userService.searchUsers(this.searchQuery).pipe(
      finalize(() => this.isSearching = false)
    ).subscribe({
      next: (response) => {
        this.searchResults = response.content;
      },
      error: (error) => {
        console.error('Error searching users:', error);
      }
    });
  }

  addContact(user: User): void {
    this.userService.addContact(user.authServerUserId).subscribe({
      next: (contact) => {
        this.contacts.push(contact);
        this.searchResults = this.searchResults.filter(u => u.authServerUserId !== user.authServerUserId);
        this.selectContact(contact);
      },
      error: (error) => {
        console.error('Error adding contact:', error);
      }
    });
  }

  // Message methods
  loadMessages(conversationId: string): void {
    this.isLoadingMessages = true;
    this.currentPage = 0;
    this.messages = [];

    this.messageService.getMessagesForConversation(conversationId).pipe(
      finalize(() => this.isLoadingMessages = false)
    ).subscribe({
      next: (response) => {
        this.messages = response.content;
        this.totalPages = response.totalPages;
        this.scrollToBottom();
      },
      error: (error) => {
        console.error('Error loading messages:', error);
      }
    });
  }

  loadMoreMessages(): void {
    if (this.currentPage >= this.totalPages - 1 || !this.selectedContact) {
      return;
    }

    this.isLoadingMessages = true;
    this.currentPage++;

    this.messageService.getMessagesForConversation(
      this.selectedContact.conversationId,
      this.currentPage
    ).pipe(
      finalize(() => this.isLoadingMessages = false)
    ).subscribe({
      next: (response) => {
        this.messages = [...this.messages, ...response.content];
      },
      error: (error) => {
        console.error('Error loading more messages:', error);
        this.currentPage--;
      }
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim() || !this.selectedContact || !this.currentUser) {
      return;
    }

    this.messageService.sendMessage(
      this.selectedContact.conversationId,
      this.selectedContact.contact.authServerUserId,
      this.newMessage
    ).subscribe({
      next: (message) => {
        this.messages.unshift(message);
        this.newMessage = '';
        this.scrollToBottom();
      },
      error: (error) => {
        console.error('Error sending message:', error);
      }
    });
  }

  // Helper methods
  getContactName(userId: string): string {
    if (!this.selectedContact) {
      return '';
    }
    return userId === this.currentUser?.authServerUserId
      ? 'You'
      : `${this.selectedContact.contact.firstName} ${this.selectedContact.contact.lastName}`;
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
      }
    }, 100);
  }
}
