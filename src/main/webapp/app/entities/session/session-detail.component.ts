import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager , JhiLanguageService  } from 'ng-jhipster';

import { Session } from './session.model';
import { SessionService } from './session.service';

@Component({
    selector: 'jhi-session-detail',
    templateUrl: './session-detail.component.html'
})
export class SessionDetailComponent implements OnInit, OnDestroy {

    session: Session;
    private subscription: any;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: EventManager,
        private jhiLanguageService: JhiLanguageService,
        private sessionService: SessionService,
        private route: ActivatedRoute
    ) {
        this.jhiLanguageService.setLocations(['session']);
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSessions();
    }

    load(id) {
        this.sessionService.find(id).subscribe((session) => {
            this.session = session;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSessions() {
        this.eventSubscriber = this.eventManager.subscribe('sessionListModification', (response) => this.load(this.session.id));
    }
}
