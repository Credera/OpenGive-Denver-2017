import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { School } from './school.model';
import { SchoolPopupService } from './school-popup.service';
import { SchoolService } from './school.service';
import { Address, AddressService } from '../address';

@Component({
    selector: 'jhi-school-dialog',
    templateUrl: './school-dialog.component.html'
})
export class SchoolDialogComponent implements OnInit {

    school: School;
    authorities: any[];
    isSaving: boolean;

    addresses: Address[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private schoolService: SchoolService,
        private addressService: AddressService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['school']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.addressService.query({filter: 'school-is-null'}).subscribe((res: Response) => {
            if (!this.school.address || !this.school.address.id) {
                this.addresses = res.json();
            } else {
                this.addressService.find(this.school.address.id).subscribe((subRes: Address) => {
                    this.addresses = [subRes].concat(res.json());
                }, (subRes: Response) => this.onError(subRes.json()));
            }
        }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.school.id !== undefined) {
            this.schoolService.update(this.school)
                .subscribe((res: School) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.schoolService.create(this.school)
                .subscribe((res: School) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: School) {
        this.eventManager.broadcast({ name: 'schoolListModification', content: 'OK'});
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

    trackAddressById(index: number, item: Address) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-school-popup',
    template: ''
})
export class SchoolPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private schoolPopupService: SchoolPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.schoolPopupService
                    .open(SchoolDialogComponent, params['id']);
            } else {
                this.modalRef = this.schoolPopupService
                    .open(SchoolDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
