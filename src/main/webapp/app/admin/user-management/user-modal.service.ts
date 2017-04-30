import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { Response } from '@angular/http';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { User, UserService, Principal } from '../../shared';
import { Role } from "../../app.constants";
import { OrganizationService } from "../../entities/organization/organization.service";
import { Observable } from "rxjs/Observable";
import { Organization } from "../../entities/organization/index";

@Injectable()
export class UserModalService {
    private isOpen = false;
    private orgsObservable: Observable<Organization>

    constructor(
        private modalService: NgbModal,
        private router: Router,
        private userService: UserService,
        private principal: Principal,
        private orgService: OrganizationService
    ) {
        this.orgsObservable = this.orgService
            .query({
                page: 0,
                size: 20,
                sort: ['name']
            })
            .map((res: Response) => res.json());
    }

    getOrganizations() : Observable<Organization> {
        return this.orgsObservable;
    }

    open(component: Component, login?: string): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (login) {
            this.userService.find(login).subscribe((user) => this.userModalRef(component, user));
        } else {
            return this.userModalRef(component, new User());
        }
    }

    openNew(component: Component, role: string): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        let user = new User();
        user.authorities = [role];

        if (this.principal.collectionHasAtLeastAuthority(user.authorities, Role.Instructor))
            user.is14Plus = true;

        this.userModalRef(component, user);
    }

    userModalRef(component: Component, user: User): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static' });
        modalRef.componentInstance.user = user;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
