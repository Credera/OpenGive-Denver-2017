import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { AchievementComponent } from './achievement.component';
import { AchievementDetailComponent } from './achievement-detail.component';
import { AchievementPopupComponent } from './achievement-dialog.component';
import { AchievementDeactivatePopupComponent } from './achievement-deactivate-dialog.component';
import { Role } from '../../app.constants';

import { Principal } from '../../shared';

export const achievementRoute: Routes = [
  {
    path: 'achievement',
    component: AchievementComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.achievement.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'achievement/:id',
    component: AchievementDetailComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.achievement.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const achievementPopupRoute: Routes = [
  {
    path: 'achievement-new',
    component: AchievementPopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.achievement.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'achievement/:id/edit',
    component: AchievementPopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.achievement.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'achievement/:id/deactivate',
    component: AchievementDeactivatePopupComponent,
    data: {
        authenticate: true,
        pageTitle: 'opengiveApp.achievement.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
