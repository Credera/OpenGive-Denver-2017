import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { CourseComponent } from './course.component';
import { CourseDetailComponent } from './course-detail.component';
import { CoursePopupComponent } from './course-dialog.component';
import { CourseDeactivatePopupComponent } from './course-deactivate-dialog.component';

import { Principal } from '../../shared';
import { Role } from '../../app.constants';

export const courseRoute: Routes = [
  {
    path: 'course',
    component: CourseComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.course.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'course/:id',
    component: CourseDetailComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.course.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const coursePopupRoute: Routes = [
  {
    path: 'course-new',
    component: CoursePopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.course.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'course/:id/edit',
    component: CoursePopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.course.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'course/:id/deactivate',
    component: CourseDeactivatePopupComponent,
    data: {
        authorities: [Role.User],
        pageTitle: 'opengiveApp.course.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
