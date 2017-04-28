import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { Milestone } from './milestone.model';
import { MilestonePopupService } from './milestone-popup.service';
import { MilestoneService } from './milestone.service';
import { Program, ProgramService } from '../program';
import { Role } from '../../app.constants';

@Component({
    selector: 'jhi-milestone-dialog',
    templateUrl: './milestone-dialog.component.html'
})
export class MilestoneDialogComponent implements OnInit {

    milestone: Milestone;
    authorities: any[];
    isSaving: boolean;

    programs: Program[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private milestoneService: MilestoneService,
        private programService: ProgramService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['milestone']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = [Role.User, Role.Admin];
        this.programService.query().subscribe(
            (res: Response) => { this.programs = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.milestone.id !== undefined) {
            this.milestoneService.update(this.milestone)
                .subscribe((res: Milestone) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.milestoneService.create(this.milestone)
                .subscribe((res: Milestone) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: Milestone) {
        this.eventManager.broadcast({ name: 'milestoneListModification', content: 'OK'});
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

    trackProgramById(index: number, item: Program) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-milestone-popup',
    template: ''
})
export class MilestonePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private milestonePopupService: MilestonePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.milestonePopupService
                    .open(MilestoneDialogComponent, params['id']);
            } else {
                this.modalRef = this.milestonePopupService
                    .open(MilestoneDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
