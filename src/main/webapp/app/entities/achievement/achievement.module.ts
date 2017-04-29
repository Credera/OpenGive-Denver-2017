import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OpengiveSharedModule } from '../../shared';
import { OpengiveAdminModule } from '../../admin/admin.module';
import {
    AchievementService,
    AchievementPopupService,
    AchievementComponent,
    AchievementDetailComponent,
    AchievementDialogComponent,
    AchievementPopupComponent,
    AchievementDeactivatePopupComponent,
    AchievementDeactivateDialogComponent,
    achievementRoute,
    achievementPopupRoute,
} from './';

const ENTITY_STATES = [
    ...achievementRoute,
    ...achievementPopupRoute,
];

@NgModule({
    imports: [
        OpengiveSharedModule,
        OpengiveAdminModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        AchievementComponent,
        AchievementDetailComponent,
        AchievementDialogComponent,
        AchievementDeactivateDialogComponent,
        AchievementPopupComponent,
        AchievementDeactivatePopupComponent,
    ],
    entryComponents: [
        AchievementComponent,
        AchievementDialogComponent,
        AchievementPopupComponent,
        AchievementDeactivateDialogComponent,
        AchievementDeactivatePopupComponent,
    ],
    providers: [
        AchievementService,
        AchievementPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengiveAchievementModule {}
