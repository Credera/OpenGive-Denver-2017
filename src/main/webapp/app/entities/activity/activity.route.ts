import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { ActivityComponent } from './activity.component';
import { ActivityDetailComponent } from './activity-detail.component';
import { ActivityPopupComponent } from './activity-dialog.component';
import { ActivityDeactivatePopupComponent } from './activity-deactivate-dialog.component';

export const activityRoute: Routes = [
  {
    path: 'activity',
    component: ActivityComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.activity.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'activity/:id',
    component: ActivityDetailComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.activity.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const activityPopupRoute: Routes = [
  {
    path: 'activity-new',
    component: ActivityPopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.activity.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'activity/:id/edit',
    component: ActivityPopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.activity.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'activity/:id/deactivate',
    component: ActivityDeactivatePopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.activity.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
