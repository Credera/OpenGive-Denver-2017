import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { PortfolioItem } from './portfolio-item.model';
import { PortfolioItemPopupService } from './portfolio-item-popup.service';
import { PortfolioItemService } from './portfolio-item.service';
import { Portfolio, PortfolioService } from '../portfolio';
import { Course, CourseService } from '../course';
import { ItemLink, ItemLinkService } from '../item-link';

@Component({
    selector: 'jhi-portfolio-item-dialog',
    templateUrl: './portfolio-item-dialog.component.html'
})
export class PortfolioItemDialogComponent implements OnInit {

    portfolioItem: PortfolioItem;
    authorities: any[];
    isSaving: boolean;

    portfolios: Portfolio[];

    courses: Course[];

    itemlinks: ItemLink[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private portfolioItemService: PortfolioItemService,
        private portfolioService: PortfolioService,
        private courseService: CourseService,
        private itemLinkService: ItemLinkService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['portfolioItem']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.portfolioService.query().subscribe(
            (res: Response) => { this.portfolios = res.json(); }, (res: Response) => this.onError(res.json()));
        this.courseService.query().subscribe(
            (res: Response) => { this.courses = res.json(); }, (res: Response) => this.onError(res.json()));
        this.itemLinkService.query().subscribe(
            (res: Response) => { this.itemlinks = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.portfolioItem.id !== undefined) {
            this.portfolioItemService.update(this.portfolioItem)
                .subscribe((res: PortfolioItem) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.portfolioItemService.create(this.portfolioItem)
                .subscribe((res: PortfolioItem) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: PortfolioItem) {
        this.eventManager.broadcast({ name: 'portfolioItemListModification', content: 'OK'});
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

    trackItemLinkById(index: number, item: ItemLink) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}

@Component({
    selector: 'jhi-portfolio-item-popup',
    template: ''
})
export class PortfolioItemPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private portfolioItemPopupService: PortfolioItemPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.portfolioItemPopupService
                    .open(PortfolioItemDialogComponent, params['id']);
            } else {
                this.modalRef = this.portfolioItemPopupService
                    .open(PortfolioItemDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
