import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { AddressComponent } from './address.component';
import { AddressDetailComponent } from './address-detail.component';
import { AddressPopupComponent } from './address-dialog.component';
import { AddressDeactivatePopupComponent } from './address-deactivate-dialog.component';

import { Principal } from '../../shared';
import { Role } from '../../app.constants';

export const addressRoute: Routes = [
  {
    path: 'address',
    component: AddressComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.address.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'address/:id',
    component: AddressDetailComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.address.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const addressPopupRoute: Routes = [
  {
    path: 'address-new',
    component: AddressPopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.address.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'address/:id/edit',
    component: AddressPopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.address.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'address/:id/deactivate',
    component: AddressDeactivatePopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.address.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
