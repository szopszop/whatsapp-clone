export interface Contact {
  id: string;
  requesterId: string;
  targetId: string;
  requesterName: string;
  targetName: string;
  status: ContactStatus;
  createdAt: string;
  updatedAt: string;
}

export enum ContactStatus {
  PENDING = 'PENDING',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED'
}
