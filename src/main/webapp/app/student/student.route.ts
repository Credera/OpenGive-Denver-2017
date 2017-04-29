import { Route } from '@angular/router';

import { UserRouteAccessService } from '../shared';
import { StudentComponent } from './student.component';

export const STUDENT_ROUTE: Route = {
  path: 'student',
  component: StudentComponent,
  data: {
    authorities: [],
    pageTitle: 'home.title'
  }
};
