import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {map, take} from 'rxjs';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAdmin$.pipe(
    take(1),
    map(isAdmin => {
      if (isAdmin) {
        return true;
      }
      router.navigate(['/chat']);
      return false;
    })
  );
};
