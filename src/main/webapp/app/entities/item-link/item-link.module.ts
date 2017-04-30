import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OpengiveSharedModule } from '../../shared';
import {
    ItemLinkService,
    ItemLinkPopupService,
    ItemLinkComponent,
    ItemLinkDetailComponent,
    ItemLinkDialogComponent,
    ItemLinkPopupComponent,
    ItemLinkDeactivatePopupComponent,
    ItemLinkDeactivateDialogComponent,
    itemLinkRoute,
    itemLinkPopupRoute,
} from './';

const ENTITY_STATES = [
    ...itemLinkRoute,
    ...itemLinkPopupRoute,
];

@NgModule({
    imports: [
        OpengiveSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        ItemLinkComponent,
        ItemLinkDetailComponent,
        ItemLinkDialogComponent,
        ItemLinkDeactivateDialogComponent,
        ItemLinkPopupComponent,
        ItemLinkDeactivatePopupComponent,
    ],
    entryComponents: [
        ItemLinkComponent,
        ItemLinkDialogComponent,
        ItemLinkPopupComponent,
        ItemLinkDeactivateDialogComponent,
        ItemLinkDeactivatePopupComponent,
    ],
    providers: [
        ItemLinkService,
        ItemLinkPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengiveItemLinkModule {}
