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
    PortfolioItemDeletePopupComponent,
    PortfolioItemDeleteDialogComponent,
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
        PortfolioItemDeleteDialogComponent,
        PortfolioItemPopupComponent,
        PortfolioItemDeletePopupComponent,
    ],
    entryComponents: [
        PortfolioItemComponent,
        PortfolioItemDialogComponent,
        PortfolioItemPopupComponent,
        PortfolioItemDeleteDialogComponent,
        PortfolioItemDeletePopupComponent,
    ],
    providers: [
        PortfolioItemService,
        PortfolioItemPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengivePortfolioItemModule {}
