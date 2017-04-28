import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { OrganizationComponent } from './organization.component';
import { OrganizationDetailComponent } from './organization-detail.component';
import { OrganizationPopupComponent } from './organization-dialog.component';
import { OrganizationDeletePopupComponent } from './organization-delete-dialog.component';

import { Principal } from '../../shared';
import { Role } from '../../app.constants';

export const organizationRoute: Routes = [
  {
    path: 'organization',
    component: OrganizationComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.organization.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'organization/:id',
    component: OrganizationDetailComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.organization.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const organizationPopupRoute: Routes = [
  {
    path: 'organization-new',
    component: OrganizationPopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.organization.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'organization/:id/edit',
    component: OrganizationPopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.organization.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'organization/:id/delete',
    component: OrganizationDeletePopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.organization.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
