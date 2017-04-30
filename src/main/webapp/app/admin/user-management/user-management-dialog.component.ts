import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { UserModalService } from './user-modal.service';
import { JhiLanguageHelper, User, UserService } from '../../shared';
import { Role } from '../../app.constants'
import { Observable } from "rxjs/Observable";
import { Organization } from "../../entities/organization/index";

@Component({
    selector: 'jhi-user-mgmt-dialog',
    templateUrl: './user-management-dialog.component.html'
})
export class UserMgmtDialogComponent implements OnInit {
    isAdmin: boolean;
    user: User;
    confirmPassword: string;
    isSaving: Boolean;
    doPasswordsMatch = true;
    formType: string;
    hide14: boolean;
    showPassword: boolean;
    authorities = [Role.Admin, Role.OrgAdmin, Role.Instructor, Role.Student].map(a => { 
        return {value: a, text: UserService.translateRole(a)};
    });
    organizations: Observable<Organization>
    

    constructor(
        public activeModal: NgbActiveModal,
        private languageHelper: JhiLanguageHelper,
        private jhiLanguageService: JhiLanguageService,
        private userService: UserService,
        private eventManager: EventManager,
        private userModalService: UserModalService
    ) { }

    ngOnInit() {
        this.isSaving = false;
        this.jhiLanguageService.setLocations(['user-management']);
        this.showPassword = this.user.id == null;
        this.formType = this.user.id == null
            ? 'Create'
            : 'Edit';

        this.hide14 = this.user.authorities.every(a => a !== Role.Student);
        this.isAdmin = this.user.authorities.some(a => a === Role.Admin);

        this.organizations = this.userModalService.getOrganizations()
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;

        if (this.user.password !== this.confirmPassword)
            return;

        if (this.user.id !== null) {
            this.userService.update(this.user).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        } else {
            this.userService.create(this.user).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        }
    }

    changePassword() {
        if (this.user.password !== this.confirmPassword) {
            this.doPasswordsMatch = false;
        } else {
            this.doPasswordsMatch = true;
        }
    }

    private onSaveSuccess(result) {
        this.eventManager.broadcast({ name: 'userListModification', content: 'OK' });
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-user-dialog',
    template: '<div></div>'
})
export class UserDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private userModalService: UserModalService
    ) { }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if (params['login']) {
                this.modalRef = this.userModalService.open(UserMgmtDialogComponent, params['login']);
            } else if (params['type']) {
                this.modalRef = this.userModalService.openNew(UserMgmtDialogComponent, params['type']);
            } else {
                this.modalRef = this.userModalService.open(UserMgmtDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
