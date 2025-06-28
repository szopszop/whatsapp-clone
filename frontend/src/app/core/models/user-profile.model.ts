export interface UserProfile {
  sub: string;
  name: string;
  email: string;
  preferred_username: string;
  email_verified: boolean;
  roles: string[];
}
