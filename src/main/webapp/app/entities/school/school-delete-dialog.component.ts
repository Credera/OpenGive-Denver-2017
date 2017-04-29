import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { School } from './school.model';
import { SchoolPopupService } from './school-popup.service';
import { SchoolService } from './school.service';

@Component({
    selector: 'jhi-school-delete-dialog',
    templateUrl: './school-delete-dialog.component.html'
})
export class SchoolDeleteDialogComponent {

    school: School;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private schoolService: SchoolService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['school']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.schoolService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'schoolListModification',
                content: 'Deleted an school'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-school-delete-popup',
    template: ''
})
export class SchoolDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private schoolPopupService: SchoolPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.schoolPopupService
                .open(SchoolDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
