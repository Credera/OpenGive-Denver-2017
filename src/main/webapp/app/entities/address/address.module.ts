import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OpengiveSharedModule } from '../../shared';
import {
    AddressService,
    AddressPopupService,
    AddressComponent,
    AddressDetailComponent,
    AddressDialogComponent,
    AddressPopupComponent,
    AddressDeactivatePopupComponent,
    AddressDeactivateDialogComponent,
    addressRoute,
    addressPopupRoute,
} from './';

const ENTITY_STATES = [
    ...addressRoute,
    ...addressPopupRoute,
];

@NgModule({
    imports: [
        OpengiveSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        AddressComponent,
        AddressDetailComponent,
        AddressDialogComponent,
        AddressDeactivateDialogComponent,
        AddressPopupComponent,
        AddressDeactivatePopupComponent,
    ],
    entryComponents: [
        AddressComponent,
        AddressDialogComponent,
        AddressPopupComponent,
        AddressDeactivateDialogComponent,
        AddressDeactivatePopupComponent,
    ],
    providers: [
        AddressService,
        AddressPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OpengiveAddressModule {}
