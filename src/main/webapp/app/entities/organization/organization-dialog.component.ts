import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { Organization } from './organization.model';
import { OrganizationPopupService } from './organization-popup.service';
import { OrganizationService } from './organization.service';
import { Role } from '../../app.constants';

@Component({
    selector: 'jhi-organization-dialog',
    templateUrl: './organization-dialog.component.html'
})
export class OrganizationDialogComponent implements OnInit {

    organization: Organization;
    isSaving: boolean;
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private organizationService: OrganizationService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['organization']);
    }

    ngOnInit() {
        this.isSaving = false;
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.organization.id !== undefined) {
            this.organizationService.update(this.organization)
                .subscribe((res: Organization) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.organizationService.create(this.organization)
                .subscribe((res: Organization) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: Organization) {
        this.eventManager.broadcast({ name: 'organizationListModification', content: 'OK'});
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
}

@Component({
    selector: 'jhi-organization-popup',
    template: ''
})
export class OrganizationPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private organizationPopupService: OrganizationPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.organizationPopupService
                    .open(OrganizationDialogComponent, params['id']);
            } else {
                this.modalRef = this.organizationPopupService
                    .open(OrganizationDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
