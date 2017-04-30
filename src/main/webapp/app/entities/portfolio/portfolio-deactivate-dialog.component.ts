import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { Portfolio } from './portfolio.model';
import { PortfolioPopupService } from './portfolio-popup.service';
import { PortfolioService } from './portfolio.service';

@Component({
    selector: 'jhi-portfolio-deactivate-dialog',
    templateUrl: './portfolio-deactivate-dialog.component.html'
})
export class PortfolioDeactivateDialogComponent {

    portfolio: Portfolio;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private portfolioService: PortfolioService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['portfolio']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDeactivate(id: number) {
        this.portfolioService.deactivate(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'portfolioListModification',
                content: 'Deactivated an portfolio'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-portfolio-deactivate-popup',
    template: ''
})
export class PortfolioDeactivatePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private portfolioPopupService: PortfolioPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.portfolioPopupService
                .open(PortfolioDeactivateDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
