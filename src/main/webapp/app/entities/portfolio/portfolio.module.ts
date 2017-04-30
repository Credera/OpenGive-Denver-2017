import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OpengiveSharedModule } from '../../shared';
import { OpengiveAdminModule } from '../../admin/admin.module';
import {
    PortfolioService,
    PortfolioPopupService,
    PortfolioComponent,
    PortfolioDetailComponent,
    PortfolioDialogComponent,
    PortfolioPopupComponent,
    PortfolioDeactivatePopupComponent,
    PortfolioDeactivateDialogComponent,
    portfolioRoute,
    portfolioPopupRoute,
} from './';

const ENTITY_STATES = [
    ...portfolioRoute,
    ...portfolioPopupRoute,
];

@NgModule({
    imports: [
        OpengiveSharedModule,
        OpengiveAdminModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        PortfolioComponent,
        PortfolioDetailComponent,
        PortfolioDialogComponent,
        PortfolioDeactivateDialogComponent,
        PortfolioPopupComponent,
        PortfolioDeactivatePopupComponent,
    ],
    entryComponents: [
        PortfolioComponent,
        PortfolioDialogComponent,
        PortfolioPopupComponent,
        PortfolioDeactivateDialogComponent,
        PortfolioDeactivatePopupComponent,
    ],
    providers: [
        PortfolioService,
        PortfolioPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengivePortfolioModule {}
