export interface User {
  id: string;
  authServerUserId: string;
  email: string;
  roles: string[];
  createdAt: string;
  firstName?: string;
  lastName?: string;
  profilePictureUrl?: string;
  about?: string;
}
