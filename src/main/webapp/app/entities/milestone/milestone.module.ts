import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OpengiveSharedModule } from '../../shared';
import {
    MilestoneService,
    MilestonePopupService,
    MilestoneComponent,
    MilestoneDetailComponent,
    MilestoneDialogComponent,
    MilestonePopupComponent,
    MilestoneDeactivatePopupComponent,
    MilestoneDeactivateDialogComponent,
    milestoneRoute,
    milestonePopupRoute,
} from './';

const ENTITY_STATES = [
    ...milestoneRoute,
    ...milestonePopupRoute,
];

@NgModule({
    imports: [
        OpengiveSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        MilestoneComponent,
        MilestoneDetailComponent,
        MilestoneDialogComponent,
        MilestoneDeactivateDialogComponent,
        MilestonePopupComponent,
        MilestoneDeactivatePopupComponent,
    ],
    entryComponents: [
        MilestoneComponent,
        MilestoneDialogComponent,
        MilestonePopupComponent,
        MilestoneDeactivateDialogComponent,
        MilestoneDeactivatePopupComponent,
    ],
    providers: [
        MilestoneService,
        MilestonePopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengiveMilestoneModule {}
