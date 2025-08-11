export interface User {
  id: string;
  authServerUserId: string;
  email: string;
  firstName: string;
  lastName: string;
  profilePictureUrl?: string;
  about?: string;
  status: string;
  roles: string[];
}

export interface Contact {
  id: string;
  contact: User;
  conversationId: string;
}

export interface Message {
  id: string;
  conversationId: string;
  senderId: string;
  recipientId: string;
  content: string;
  status: string;
  createdAt: string;
}
