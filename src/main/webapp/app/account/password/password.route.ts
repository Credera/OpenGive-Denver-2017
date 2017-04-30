import { Route } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PasswordComponent } from './password.component';
import { Role } from '../../app.constants';

export const passwordRoute: Route = {
  path: 'password',
  component: PasswordComponent,
  data: {
      authenticate: true,
    pageTitle: 'global.menu.account.password'
  },
  canActivate: [UserRouteAccessService]
};
