export interface Message {
  id: string;
  senderId: string;
  recipientId: string;
  conversationId: string;
  content: string;
  timestamp: string;
  read: boolean;
  senderName?: string;
}

export interface SendMessageRequest {
  recipientId: string;
  content: string;
  conversationId?: string;
}
