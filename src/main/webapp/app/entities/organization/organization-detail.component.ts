import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager , JhiLanguageService  } from 'ng-jhipster';

import { Organization } from './organization.model';
import { OrganizationService } from './organization.service';
import { Role } from "../../app.constants";

@Component({
    selector: 'jhi-organization-detail',
    templateUrl: './organization-detail.component.html'
})
export class OrganizationDetailComponent implements OnInit, OnDestroy {

    organization: Organization;
    role: any;
    private subscription: any;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: EventManager,
        private jhiLanguageService: JhiLanguageService,
        private organizationService: OrganizationService,
        private route: ActivatedRoute
    ) {
        this.jhiLanguageService.setLocations(['organization']);
        this.role = Role;
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInOrganizations();
    }

    load(id) {
        this.organizationService.find(id).subscribe((organization) => {
            this.organization = organization;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInOrganizations() {
        this.eventSubscriber = this.eventManager.subscribe('organizationListModification', (response) => this.load(this.organization.id));
    }
}
