import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { ProgramComponent } from './program.component';
import { ProgramDetailComponent } from './program-detail.component';
import { ProgramPopupComponent } from './program-dialog.component';
import { ProgramDeletePopupComponent } from './program-delete-dialog.component';

import { Principal } from '../../shared';

export const programRoute: Routes = [
  {
    path: 'program',
    component: ProgramComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.program.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'program/:id',
    component: ProgramDetailComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.program.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const programPopupRoute: Routes = [
  {
    path: 'program-new',
    component: ProgramPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.program.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'program/:id/edit',
    component: ProgramPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.program.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'program/:id/delete',
    component: ProgramDeletePopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'opengiveApp.program.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
