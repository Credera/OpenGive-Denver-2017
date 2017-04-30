import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { Achievement } from './achievement.model';
import { AchievementPopupService } from './achievement-popup.service';
import { AchievementService } from './achievement.service';
import { Activity, ActivityService } from '../activity';

@Component({
    selector: 'jhi-achievement-dialog',
    templateUrl: './achievement-dialog.component.html'
})
export class AchievementDialogComponent implements OnInit {

    achievement: Achievement;
    isSaving: boolean;

    activities: Activity[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private achievementService: AchievementService,
        private activityService: ActivityService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['achievement']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.activityService.query().subscribe(
            (res: Response) => { this.activities = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.achievement.id !== undefined) {
            this.achievementService.update(this.achievement)
                .subscribe((res: Achievement) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.achievementService.create(this.achievement)
                .subscribe((res: Achievement) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: Achievement) {
        this.eventManager.broadcast({ name: 'achievementListModification', content: 'OK'});
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

    trackActivityById(index: number, item: Activity) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-achievement-popup',
    template: ''
})
export class AchievementPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private achievementPopupService: AchievementPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.achievementPopupService
                    .open(AchievementDialogComponent, params['id']);
            } else {
                this.modalRef = this.achievementPopupService
                    .open(AchievementDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
