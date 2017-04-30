import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { Portfolio } from './portfolio.model';
import { PortfolioPopupService } from './portfolio-popup.service';
import { PortfolioService } from './portfolio.service';
import { User, UserService } from '../../shared';
import { Role } from '../../app.constants';

@Component({
    selector: 'jhi-portfolio-dialog',
    templateUrl: './portfolio-dialog.component.html'
})
export class PortfolioDialogComponent implements OnInit {

    portfolio: Portfolio;
    isSaving: boolean;

    users: User[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private portfolioService: PortfolioService,
        private userService: UserService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['portfolio']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.userService.query().subscribe(
            (res: Response) => { this.users = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.portfolio.id !== undefined) {
            this.portfolioService.update(this.portfolio)
                .subscribe((res: Portfolio) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.portfolioService.create(this.portfolio)
                .subscribe((res: Portfolio) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: Portfolio) {
        this.eventManager.broadcast({ name: 'portfolioListModification', content: 'OK'});
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

    trackUserById(index: number, item: User) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-portfolio-popup',
    template: ''
})
export class PortfolioPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private portfolioPopupService: PortfolioPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.portfolioPopupService
                    .open(PortfolioDialogComponent, params['id']);
            } else {
                this.modalRef = this.portfolioPopupService
                    .open(PortfolioDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
