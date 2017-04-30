import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { Address } from './address.model';
import { AddressPopupService } from './address-popup.service';
import { AddressService } from './address.service';

@Component({
    selector: 'jhi-address-deactivate-dialog',
    templateUrl: './address-deactivate-dialog.component.html'
})
export class AddressDeactivateDialogComponent {

    address: Address;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private addressService: AddressService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['address', 'state']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDeactivate(id: number) {
        this.addressService.deactivate(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'addressListModification',
                content: 'Deactivated an address'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-address-deactivate-popup',
    template: ''
})
export class AddressDeactivatePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private addressPopupService: AddressPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.addressPopupService
                .open(AddressDeactivateDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
