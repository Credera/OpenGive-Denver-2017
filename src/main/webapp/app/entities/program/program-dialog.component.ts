import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { Program } from './program.model';
import { ProgramPopupService } from './program-popup.service';
import { ProgramService } from './program.service';
import { Organization, OrganizationService } from '../organization';
import { User, UserService } from '../../shared';
import { Role } from '../../app.constants';

@Component({
    selector: 'jhi-program-dialog',
    templateUrl: './program-dialog.component.html'
})
export class ProgramDialogComponent implements OnInit {

    program: Program;
    authorities: any[];
    isSaving: boolean;

    organizations: Organization[];

    users: User[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private programService: ProgramService,
        private organizationService: OrganizationService,
        private userService: UserService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['program']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = [Role.User, Role.Admin];
        this.organizationService.query().subscribe(
            (res: Response) => { this.organizations = res.json(); }, (res: Response) => this.onError(res.json()));
        this.userService.query().subscribe(
            (res: Response) => { this.users = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.program.id !== undefined) {
            this.programService.update(this.program)
                .subscribe((res: Program) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.programService.create(this.program)
                .subscribe((res: Program) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: Program) {
        this.eventManager.broadcast({ name: 'programListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }

    trackOrganizationById(index: number, item: Organization) {
        return item.id;
    }

    trackUserById(index: number, item: User) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-program-popup',
    template: ''
})
export class ProgramPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private programPopupService: ProgramPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.programPopupService
                    .open(ProgramDialogComponent, params['id']);
            } else {
                this.modalRef = this.programPopupService
                    .open(ProgramDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
