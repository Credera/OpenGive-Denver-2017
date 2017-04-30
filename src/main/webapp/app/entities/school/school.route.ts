import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { SchoolComponent } from './school.component';
import { SchoolDetailComponent } from './school-detail.component';
import { SchoolPopupComponent } from './school-dialog.component';
import { SchoolDeactivatePopupComponent } from './school-deactivate-dialog.component';

import { Principal } from '../../shared';

export const schoolRoute: Routes = [
  {
    path: 'school',
    component: SchoolComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.school.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'school/:id',
    component: SchoolDetailComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.school.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const schoolPopupRoute: Routes = [
  {
    path: 'school-new',
    component: SchoolPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.school.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'school/:id/edit',
    component: SchoolPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.school.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'school/:id/deactivate',
    component: SchoolDeactivatePopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.school.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
