import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';

import { AuthService } from '../';
import { Principal } from '../';
import { LoginModalService } from '../login/login-modal.service';
import { StateStorageService } from './state-storage.service';

@Injectable()
export class UserRouteAccessService implements CanActivate {

    constructor(private router: Router,
        private loginModalService: LoginModalService,
        private principal: Principal,
        private stateStorageService: StateStorageService) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | Promise<boolean> {

        const authorities = route.data['authorities'];
        if (!authorities || authorities.length === 0) {
            return true;
        }

        return this.checkLogin(authorities, state.url);
    }

    checkLogin(authorities: string[], url: string): Promise<boolean> {
        const principal = this.principal;
        let p = principal.identity().then(account => {

            if (account) {
                return principal.hasAnyAuthority(authorities).then(hasAuth => {
                    if (hasAuth)
                        return Promise.resolve(true);

                    this.stateStorageService.storeUrl(url);
                    this.router.navigate(['accessdenied']);

                    return Promise.resolve(false);
                })
            }


            return Promise.resolve(false);
        })
        return Promise.resolve(p);
    }
}
