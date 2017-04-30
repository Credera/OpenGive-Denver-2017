import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils, EventManager } from 'ng-jhipster';
import { OpengiveTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { ActivityDetailComponent } from '../../../../../../main/webapp/app/entities/activity/activity-detail.component';
import { ActivityService } from '../../../../../../main/webapp/app/entities/activity/activity.service';
import { Activity } from '../../../../../../main/webapp/app/entities/activity/activity.model';

describe('Component Tests', () => {

    describe('Activity Management Detail Component', () => {
        let comp: ActivityDetailComponent;
        let fixture: ComponentFixture<ActivityDetailComponent>;
        let service: ActivityService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [OpengiveTestModule],
                declarations: [ActivityDetailComponent],
                providers: [
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    ActivityService,
                    EventManager
                ]
            }).overrideComponent(ActivityDetailComponent, {
                set: {
                    template: ''
                }
            }).compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(ActivityDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ActivityService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Activity(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.activity).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
