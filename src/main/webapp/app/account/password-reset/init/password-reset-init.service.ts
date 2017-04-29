import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class PasswordResetInit {

    constructor(private http: Http) {}

    save(login: string): Observable<any> {
        return this.http.post('api/account/reset_password/init', login);
    }
}
