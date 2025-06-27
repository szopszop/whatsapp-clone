export interface IdentityClaims {
  sub: string;
  name: string;
  preferred_username: string;
  email: string;
  email_verified?: boolean;
  roles?: string[];
}
