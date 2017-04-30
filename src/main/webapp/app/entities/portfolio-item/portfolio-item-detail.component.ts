import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager , JhiLanguageService  } from 'ng-jhipster';

import { PortfolioItem } from './portfolio-item.model';
import { PortfolioItemService } from './portfolio-item.service';

@Component({
    selector: 'jhi-portfolio-item-detail',
    templateUrl: './portfolio-item-detail.component.html'
})
export class PortfolioItemDetailComponent implements OnInit, OnDestroy {

    portfolioItem: PortfolioItem;
    private subscription: any;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: EventManager,
        private jhiLanguageService: JhiLanguageService,
        private portfolioItemService: PortfolioItemService,
        private route: ActivatedRoute
    ) {
        this.jhiLanguageService.setLocations(['portfolioItem']);
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInPortfolioItems();
    }

    load(id) {
        this.portfolioItemService.find(id).subscribe((portfolioItem) => {
            this.portfolioItem = portfolioItem;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInPortfolioItems() {
        this.eventSubscriber = this.eventManager.subscribe('portfolioItemListModification', (response) => this.load(this.portfolioItem.id));
    }
}
