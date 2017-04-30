import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { OrganizationComponent } from './organization.component';
import { OrganizationDetailComponent } from './organization-detail.component';
import { OrganizationPopupComponent } from './organization-dialog.component';
import { OrganizationDeactivatePopupComponent } from './organization-deactivate-dialog.component';

import { Principal } from '../../shared';
import { Role } from '../../app.constants';

export const organizationRoute: Routes = [
    {
        path: 'organization',
        component: OrganizationComponent,
        data: {
            authenticate: true,
            pageTitle: 'opengiveApp.organization.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'organization/:id',
        component: OrganizationDetailComponent,
        data: {
            authenticate: true,
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
            authorities: [Role.Admin, Role.OrgAdmin],
            pageTitle: 'opengiveApp.organization.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'organization/:id/edit',
        component: OrganizationPopupComponent,
        data: {
            authorities: [Role.Admin, Role.OrgAdmin],
            pageTitle: 'opengiveApp.organization.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'organization/:id/deactivate',
        component: OrganizationDeactivatePopupComponent,
        data: {
            authorities: [Role.Admin, Role.OrgAdmin],
            pageTitle: 'opengiveApp.organization.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
