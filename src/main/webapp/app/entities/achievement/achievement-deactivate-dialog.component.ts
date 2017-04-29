import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { Achievement } from './achievement.model';
import { AchievementPopupService } from './achievement-popup.service';
import { AchievementService } from './achievement.service';

@Component({
    selector: 'jhi-achievement-deactivate-dialog',
    templateUrl: './achievement-deactivate-dialog.component.html'
})
export class AchievementDeactivateDialogComponent {

    achievement: Achievement;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private achievementService: AchievementService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['achievement']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDeactivate(id: number) {
        this.achievementService.deactivate(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'achievementListModification',
                content: 'Deactivated an achievement'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-achievement-deactivate-popup',
    template: ''
})
export class AchievementDeactivatePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private achievementPopupService: AchievementPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.achievementPopupService
                .open(AchievementDeactivateDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
