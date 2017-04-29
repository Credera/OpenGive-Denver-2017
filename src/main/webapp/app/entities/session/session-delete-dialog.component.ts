import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { Session } from './session.model';
import { SessionPopupService } from './session-popup.service';
import { SessionService } from './session.service';

@Component({
    selector: 'jhi-session-delete-dialog',
    templateUrl: './session-delete-dialog.component.html'
})
export class SessionDeleteDialogComponent {

    session: Session;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private sessionService: SessionService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['session']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.sessionService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'sessionListModification',
                content: 'Deleted an session'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-session-delete-popup',
    template: ''
})
export class SessionDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private sessionPopupService: SessionPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.sessionPopupService
                .open(SessionDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}