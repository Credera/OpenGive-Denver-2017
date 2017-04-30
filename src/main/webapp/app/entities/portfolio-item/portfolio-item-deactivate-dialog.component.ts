import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { PortfolioItem } from './portfolio-item.model';
import { PortfolioItemPopupService } from './portfolio-item-popup.service';
import { PortfolioItemService } from './portfolio-item.service';

@Component({
    selector: 'jhi-portfolio-item-deactivate-dialog',
    templateUrl: './portfolio-item-deactivate-dialog.component.html'
})
export class PortfolioItemDeactivateDialogComponent {

    portfolioItem: PortfolioItem;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private portfolioItemService: PortfolioItemService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['portfolioItem']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDeactivate(id: number) {
        this.portfolioItemService.deactivate(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'portfolioItemListModification',
                content: 'Deactivated an portfolioItem'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-portfolio-item-deactivate-popup',
    template: ''
})
export class PortfolioItemDeactivatePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private portfolioItemPopupService: PortfolioItemPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.portfolioItemPopupService
                .open(PortfolioItemDeactivateDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
