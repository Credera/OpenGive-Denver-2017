import { Route } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { SettingsComponent } from './settings.component';
import { Role } from '../../app.constants';

export const settingsRoute: Route = {
  path: 'settings',
  component: SettingsComponent,
  data: {
    authorities: [Role.User],
    pageTitle: 'global.menu.account.settings'
  },
  canActivate: [UserRouteAccessService]
};
