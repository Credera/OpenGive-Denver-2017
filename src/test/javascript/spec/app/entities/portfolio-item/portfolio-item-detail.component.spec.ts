import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils, EventManager } from 'ng-jhipster';
import { OpengiveTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { PortfolioItemDetailComponent } from '../../../../../../main/webapp/app/entities/portfolio-item/portfolio-item-detail.component';
import { PortfolioItemService } from '../../../../../../main/webapp/app/entities/portfolio-item/portfolio-item.service';
import { PortfolioItem } from '../../../../../../main/webapp/app/entities/portfolio-item/portfolio-item.model';

describe('Component Tests', () => {

    describe('PortfolioItem Management Detail Component', () => {
        let comp: PortfolioItemDetailComponent;
        let fixture: ComponentFixture<PortfolioItemDetailComponent>;
        let service: PortfolioItemService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [OpengiveTestModule],
                declarations: [PortfolioItemDetailComponent],
                providers: [
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    PortfolioItemService,
                    EventManager
                ]
            }).overrideComponent(PortfolioItemDetailComponent, {
                set: {
                    template: ''
                }
            }).compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(PortfolioItemDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PortfolioItemService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new PortfolioItem(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.portfolioItem).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
