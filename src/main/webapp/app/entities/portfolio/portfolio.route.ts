import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { PortfolioComponent } from './portfolio.component';
import { PortfolioDetailComponent } from './portfolio-detail.component';
import { PortfolioPopupComponent } from './portfolio-dialog.component';
import { PortfolioDeactivatePopupComponent } from './portfolio-deactivate-dialog.component';

import { Principal } from '../../shared';
import { Role } from '../../app.constants';

export const portfolioRoute: Routes = [
  {
    path: 'portfolio',
    component: PortfolioComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.portfolio.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'portfolio/:id',
    component: PortfolioDetailComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.portfolio.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const portfolioPopupRoute: Routes = [
  {
    path: 'portfolio-new',
    component: PortfolioPopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.portfolio.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'portfolio/:id/edit',
    component: PortfolioPopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.portfolio.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'portfolio/:id/deactivate',
    component: PortfolioDeactivatePopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.portfolio.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
