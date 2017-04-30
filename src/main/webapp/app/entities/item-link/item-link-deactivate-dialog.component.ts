import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { ItemLink } from './item-link.model';
import { ItemLinkPopupService } from './item-link-popup.service';
import { ItemLinkService } from './item-link.service';

@Component({
    selector: 'jhi-item-link-deactivate-dialog',
    templateUrl: './item-link-deactivate-dialog.component.html'
})
export class ItemLinkDeactivateDialogComponent {

    itemLink: ItemLink;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private itemLinkService: ItemLinkService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['itemLink']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDeactivate(id: number) {
        this.itemLinkService.deactivate(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'itemLinkListModification',
                content: 'Deactivated an itemLink'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-item-link-deactivate-popup',
    template: ''
})
export class ItemLinkDeactivatePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private itemLinkPopupService: ItemLinkPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.itemLinkPopupService
                .open(ItemLinkDeactivateDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
