import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { Milestone } from './milestone.model';
import { MilestonePopupService } from './milestone-popup.service';
import { MilestoneService } from './milestone.service';

@Component({
    selector: 'jhi-milestone-deactivate-dialog',
    templateUrl: './milestone-deactivate-dialog.component.html'
})
export class MilestoneDeactivateDialogComponent {

    milestone: Milestone;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private milestoneService: MilestoneService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['milestone']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDeactivate(id: number) {
        this.milestoneService.deactivate(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'milestoneListModification',
                content: 'Deactivated an milestone'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-milestone-deactivate-popup',
    template: ''
})
export class MilestoneDeactivatePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private milestonePopupService: MilestonePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.milestonePopupService
                .open(MilestoneDeactivateDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
