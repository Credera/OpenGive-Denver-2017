import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { ItemLink } from './item-link.model';
import { ItemLinkPopupService } from './item-link-popup.service';
import { ItemLinkService } from './item-link.service';
import { Portfolio, PortfolioService } from '../portfolio';
import { Course, CourseService } from '../course';
import { Role } from '../../app.constants';

@Component({
    selector: 'jhi-item-link-dialog',
    templateUrl: './item-link-dialog.component.html'
})
export class ItemLinkDialogComponent implements OnInit {

    itemLink: ItemLink;
    authorities: any[];
    isSaving: boolean;

    portfolios: Portfolio[];

    courses: Course[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private itemLinkService: ItemLinkService,
        private portfolioService: PortfolioService,
        private courseService: CourseService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['itemLink']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = [Role.User, Role.Admin];
        this.portfolioService.query().subscribe(
            (res: Response) => { this.portfolios = res.json(); }, (res: Response) => this.onError(res.json()));
        this.courseService.query().subscribe(
            (res: Response) => { this.courses = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.itemLink.id !== undefined) {
            this.itemLinkService.update(this.itemLink)
                .subscribe((res: ItemLink) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.itemLinkService.create(this.itemLink)
                .subscribe((res: ItemLink) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: ItemLink) {
        this.eventManager.broadcast({ name: 'itemLinkListModification', content: 'OK'});
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

    trackPortfolioById(index: number, item: Portfolio) {
        return item.id;
    }

    trackCourseById(index: number, item: Course) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-item-link-popup',
    template: ''
})
export class ItemLinkPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private itemLinkPopupService: ItemLinkPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.itemLinkPopupService
                    .open(ItemLinkDialogComponent, params['id']);
            } else {
                this.modalRef = this.itemLinkPopupService
                    .open(ItemLinkDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
