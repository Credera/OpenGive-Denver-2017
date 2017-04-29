import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { User } from './user.model';
import { Role } from "../../app.constants";

@Injectable()
export class UserService {
    private resourceUrl = 'api/users';

    constructor(private http: Http) { }

    translateRole(roleKey) : string {
        switch (roleKey) {
            case Role.Admin:
                return 'Administrator'
            case Role.OrgAdmin:
                return 'Org Admin'
            case Role.Instructor:
                return 'Instructor'
            case Role.Student:
                return 'Student'
            default:
                return 'None'
        }
    }

    create(user: User): Observable<Response> {
        return this.http.post(this.resourceUrl, user);
    }

    update(user: User): Observable<Response> {
        return this.http.put(this.resourceUrl, user);
    }

    find(login: string): Observable<User> {
        return this.http.get(`${this.resourceUrl}/${login}`).map((res: Response) => res.json());
    }

    query(req?: any): Observable<Response> {
        const params: URLSearchParams = new URLSearchParams();
        if (req) {
            params.set('page', req.page);
            params.set('size', req.size);
            if (req.sort) {
                params.paramsMap.set('sort', req.sort);
            }
        }

        const options = {
            search: params
        };

        return this.http.get(this.resourceUrl, options);
    }

    delete(login: string): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${login}`);
    }
}
