import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { User, UserService, Principal } from '../../shared';
import { Role } from "../../app.constants";

@Injectable()
export class UserModalService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private userService: UserService,
        private principal: Principal
    ) { }

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
