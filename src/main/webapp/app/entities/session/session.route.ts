import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { SessionComponent } from './session.component';
import { SessionDetailComponent } from './session-detail.component';
import { SessionPopupComponent } from './session-dialog.component';
import { SessionDeactivatePopupComponent } from './session-deactivate-dialog.component';

import { Principal } from '../../shared';

export const sessionRoute: Routes = [
  {
    path: 'session',
    component: SessionComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.session.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'session/:id',
    component: SessionDetailComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.session.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const sessionPopupRoute: Routes = [
  {
    path: 'session-new',
    component: SessionPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.session.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'session/:id/edit',
    component: SessionPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.session.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'session/:id/deactivate',
    component: SessionDeactivatePopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.session.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
