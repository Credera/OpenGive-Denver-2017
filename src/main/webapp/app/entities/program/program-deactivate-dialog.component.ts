import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { Program } from './program.model';
import { ProgramPopupService } from './program-popup.service';
import { ProgramService } from './program.service';

@Component({
    selector: 'jhi-program-deactivate-dialog',
    templateUrl: './program-deactivate-dialog.component.html'
})
export class ProgramDeactivateDialogComponent {

    program: Program;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private programService: ProgramService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['program']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDeactivate(id: number) {
        this.programService.deactivate(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'programListModification',
                content: 'Deactivated an program'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-program-deactivate-popup',
    template: ''
})
export class ProgramDeactivatePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private programPopupService: ProgramPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.programPopupService
                .open(ProgramDeactivateDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
