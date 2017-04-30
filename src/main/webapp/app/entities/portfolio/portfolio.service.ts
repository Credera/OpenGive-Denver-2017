import { User } from './../../shared/user/user.model';
import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, BaseRequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { Portfolio } from './portfolio.model';
@Injectable()
export class PortfolioService {

    private resourceUrl = 'api/portfolios';
    private resourceSearchUrl = 'api/_search/portfolios';

    constructor(private http: Http) { }

    create(portfolio: Portfolio): Observable<Portfolio> {
        const copy: Portfolio = Object.assign({}, portfolio);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(portfolio: Portfolio): Observable<Portfolio> {
        const copy: Portfolio = Object.assign({}, portfolio);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Portfolio> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    query(req?: any): Observable<Response> {
        const options = this.createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
        ;
    }

    deactivate(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    search(req?: any): Observable<Response> {
        const options = this.createRequestOption(req);
        return this.http.get(this.resourceSearchUrl, options)
        ;
    }

    fakeData(): Portfolio[] {
    var fakeUser = new User(1, "login", "Wheeler", "Olis", "wolis@credera.com", true, ["USER_ROLE"],
        null, new Date(), "bob", new Date(), "dksaf")
        var observable = new Observable();
        var portfolio = new Portfolio(1, fakeUser);
        return [portfolio];
        
        // return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
        //     return res.json();
        // });
    }

    private createRequestOption(req?: any): BaseRequestOptions {
        const options: BaseRequestOptions = new BaseRequestOptions();
        if (req) {
            const params: URLSearchParams = new URLSearchParams();
            params.set('page', req.page);
            params.set('size', req.size);
            if (req.sort) {
                params.paramsMap.set('sort', req.sort);
            }
            params.set('query', req.query);

            options.search = params;
        }
        return options;
    }
}
