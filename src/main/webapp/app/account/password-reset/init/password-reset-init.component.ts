import { Component, OnInit, AfterViewInit, Renderer, ElementRef } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';

import { PasswordResetInit } from './password-reset-init.service';

@Component({
    selector: 'jhi-password-reset-init',
    templateUrl: './password-reset-init.component.html'
})
export class PasswordResetInitComponent implements OnInit, AfterViewInit {
    error: string;
    errorEmailNotExists: string;
    notRegistered: string;
    resetAccount: any;
    success: string;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private passwordResetInit: PasswordResetInit,
        private elementRef: ElementRef,
        private renderer: Renderer
    ) {
        this.jhiLanguageService.setLocations(['reset']);
    }

    ngOnInit() {
        this.resetAccount = {};
    }

    ngAfterViewInit() {
        this.renderer.invokeElementMethod(this.elementRef.nativeElement.querySelector('#login'), 'focus', []);
    }

    requestReset() {
        this.error = null;
        this.errorEmailNotExists = null;
        this.notRegistered = null;

        this.passwordResetInit.save(this.resetAccount.login).subscribe((response) => {
            // TODO: For MVP, we will just tell the users to contact an administrator; later on a password reset email will be sent when possible
            // if (response._body === 'No email; contact an administrator') {
                this.errorEmailNotExists = 'ERROR';
                this.error = 'ERROR';
            //
            // } else {
            //      this.success = 'OK';
            // }
        }, (response) => {
            this.success = null;
            this.error = 'ERROR';
            if (response.status === 400 && response._body === 'login not registered') {
                this.notRegistered = 'ERROR';
            }
        });
    }
}
