export interface ErrorDTO {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: { [key: string]: string };
}
