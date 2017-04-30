import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { PortfolioItemComponent } from './portfolio-item.component';
import { PortfolioItemDetailComponent } from './portfolio-item-detail.component';
import { PortfolioItemPopupComponent } from './portfolio-item-dialog.component';
import { PortfolioItemDeactivatePopupComponent } from './portfolio-item-deactivate-dialog.component';

import { Principal } from '../../shared';

export const portfolioItemRoute: Routes = [
  {
    path: 'portfolio-item',
    component: PortfolioItemComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.portfolioItem.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'portfolio-item/:id',
    component: PortfolioItemDetailComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.portfolioItem.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const portfolioItemPopupRoute: Routes = [
  {
    path: 'portfolio-item-new',
    component: PortfolioItemPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.portfolioItem.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'portfolio-item/:id/edit',
    component: PortfolioItemPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.portfolioItem.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'portfolio-item/:id/deactivate',
    component: PortfolioItemDeactivatePopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.portfolioItem.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
