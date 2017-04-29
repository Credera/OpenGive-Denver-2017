import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { OpengiveOrganizationModule } from './organization/organization.module';
import { OpengiveAddressModule } from './address/address.module';
import { OpengivePortfolioModule } from './portfolio/portfolio.module';
import { OpengiveCourseModule } from './course/course.module';
import { OpengiveMilestoneModule } from './milestone/milestone.module';
import { OpengiveAchievementModule } from './achievement/achievement.module';
import { OpengiveItemLinkModule } from './item-link/item-link.module';
import { OpengiveSessionModule } from './session/session.module';
import { OpengiveProgramModule } from './program/program.module';
import { OpengiveSchoolModule } from './school/school.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        OpengiveOrganizationModule,
        OpengiveAddressModule,
        OpengivePortfolioModule,
        OpengiveCourseModule,
        OpengiveMilestoneModule,
        OpengiveAchievementModule,
        OpengiveItemLinkModule,
        OpengiveSessionModule,
        OpengiveProgramModule,
        OpengiveSchoolModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengiveEntityModule {}
