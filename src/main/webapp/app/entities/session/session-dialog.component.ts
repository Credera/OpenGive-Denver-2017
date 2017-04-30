import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { Session } from './session.model';
import { SessionPopupService } from './session-popup.service';
import { SessionService } from './session.service';
import { Course, CourseService } from '../course';
import { Organization, OrganizationService } from '../organization';

@Component({
    selector: 'jhi-session-dialog',
    templateUrl: './session-dialog.component.html'
})
export class SessionDialogComponent implements OnInit {

    session: Session;
    authorities: any[];
    isSaving: boolean;

    courses: Course[];

    organizations: Organization[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private sessionService: SessionService,
        private courseService: CourseService,
        private organizationService: OrganizationService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['session']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.courseService.query().subscribe(
            (res: Response) => { this.courses = res.json(); }, (res: Response) => this.onError(res.json()));
        this.organizationService.query().subscribe(
            (res: Response) => { this.organizations = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.session.id !== undefined) {
            this.sessionService.update(this.session)
                .subscribe((res: Session) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.sessionService.create(this.session)
                .subscribe((res: Session) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: Session) {
        this.eventManager.broadcast({ name: 'sessionListModification', content: 'OK'});
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

    trackCourseById(index: number, item: Course) {
        return item.id;
    }

    trackOrganizationById(index: number, item: Organization) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}

@Component({
    selector: 'jhi-session-popup',
    template: ''
})
export class SessionPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private sessionPopupService: SessionPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.sessionPopupService
                    .open(SessionDialogComponent, params['id']);
            } else {
                this.modalRef = this.sessionPopupService
                    .open(SessionDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
