export interface Connection {
  id: string;
  requesterId: string;
  targetId: string;
  requesterName: string;
  targetName: string;
  status: ConnectionStatus;
  createdAt: string;
  updatedAt: string;
}

export enum ConnectionStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED'
}
