export interface Message {
  id: string;
  senderId: string;
  receiverId: string;
  conversationId: string;
  content: string;
  timestamp: string;
  read: boolean;
  senderName?: string;
}

export interface SendMessageRequest {
  receiverId: string;
  content: string;
  conversationId?: string;
}
