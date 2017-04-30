import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OpengiveSharedModule } from '../../shared';
import {
    SessionService,
    SessionPopupService,
    SessionComponent,
    SessionDetailComponent,
    SessionDialogComponent,
    SessionPopupComponent,
    SessionDeactivatePopupComponent,
    SessionDeactivateDialogComponent,
    sessionRoute,
    sessionPopupRoute,
} from './';

const ENTITY_STATES = [
    ...sessionRoute,
    ...sessionPopupRoute,
];

@NgModule({
    imports: [
        OpengiveSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        SessionComponent,
        SessionDetailComponent,
        SessionDialogComponent,
        SessionDeactivateDialogComponent,
        SessionPopupComponent,
        SessionDeactivatePopupComponent,
    ],
    entryComponents: [
        SessionComponent,
        SessionDialogComponent,
        SessionPopupComponent,
        SessionDeactivateDialogComponent,
        SessionDeactivatePopupComponent,
    ],
    providers: [
        SessionService,
        SessionPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengiveSessionModule {}
