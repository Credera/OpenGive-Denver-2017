import { Route } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { SettingsComponent } from './settings.component';
import { Role } from '../../app.constants';

export const settingsRoute: Route = {
  path: 'settings',
  component: SettingsComponent,
  data: {
    authenticate: true,
    pageTitle: 'global.menu.account.settings'
  },
  canActivate: [UserRouteAccessService]
};
