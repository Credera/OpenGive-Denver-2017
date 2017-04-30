import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OpengiveSharedModule } from '../../shared';
import {
    PortfolioItemService,
    PortfolioItemPopupService,
    PortfolioItemComponent,
    PortfolioItemDetailComponent,
    PortfolioItemDialogComponent,
    PortfolioItemPopupComponent,
    PortfolioItemDeactivatePopupComponent,
    PortfolioItemDeactivateDialogComponent,
    portfolioItemRoute,
    portfolioItemPopupRoute,
} from './';

const ENTITY_STATES = [
    ...portfolioItemRoute,
    ...portfolioItemPopupRoute,
];

@NgModule({
    imports: [
        OpengiveSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        PortfolioItemComponent,
        PortfolioItemDetailComponent,
        PortfolioItemDialogComponent,
        PortfolioItemDeactivateDialogComponent,
        PortfolioItemPopupComponent,
        PortfolioItemDeactivatePopupComponent,
    ],
    entryComponents: [
        PortfolioItemComponent,
        PortfolioItemDialogComponent,
        PortfolioItemPopupComponent,
        PortfolioItemDeactivateDialogComponent,
        PortfolioItemDeactivatePopupComponent,
    ],
    providers: [
        PortfolioItemService,
        PortfolioItemPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengivePortfolioItemModule {}
