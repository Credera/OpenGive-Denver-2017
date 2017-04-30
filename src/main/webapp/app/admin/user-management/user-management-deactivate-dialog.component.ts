import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { User, UserService } from '../../shared';
import { UserModalService } from './user-modal.service';

@Component({
    selector: 'jhi-user-mgmt-deactivate-dialog',
    templateUrl: './user-management-deactivate-dialog.component.html'
})
export class UserMgmtDeactivateDialogComponent {

    user: User;
    show14: boolean;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private userService: UserService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['user-management']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDeactivate(login) {
        this.userService.deactivate(login).subscribe((response) => {
            this.eventManager.broadcast({ name: 'userListModification',
                content: 'Deactivated a user'});
            this.activeModal.dismiss(true);
        });
    }

}

@Component({
    selector: 'jhi-user-deactivate-dialog',
    template: '<div></div>'
})
export class UserDeactivateDialogComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private userModalService: UserModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.modalRef = this.userModalService.open(UserMgmtDeactivateDialogComponent, params['login']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
