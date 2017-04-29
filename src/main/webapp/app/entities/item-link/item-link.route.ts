import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { ItemLinkComponent } from './item-link.component';
import { ItemLinkDetailComponent } from './item-link-detail.component';
import { ItemLinkPopupComponent } from './item-link-dialog.component';
import { ItemLinkDeactivatePopupComponent } from './item-link-deactivate-dialog.component';
import { Role } from '../../app.constants';

import { Principal } from '../../shared';

export const itemLinkRoute: Routes = [
  {
    path: 'item-link',
    component: ItemLinkComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.itemLink.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'item-link/:id',
    component: ItemLinkDetailComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.itemLink.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const itemLinkPopupRoute: Routes = [
  {
    path: 'item-link-new',
    component: ItemLinkPopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.itemLink.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'item-link/:id/edit',
    component: ItemLinkPopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.itemLink.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'item-link/:id/deactivate',
    component: ItemLinkDeactivatePopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.itemLink.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
