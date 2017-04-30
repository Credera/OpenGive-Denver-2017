import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OpengiveSharedModule } from '../shared';

import { STUDENT_ROUTE, StudentComponent } from './';

@NgModule({
    imports: [
        OpengiveSharedModule,
        RouterModule.forRoot([ STUDENT_ROUTE ], { useHash: true })
    ],
    declarations: [
        StudentComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengiveStudentModule {}
