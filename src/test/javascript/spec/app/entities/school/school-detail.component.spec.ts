import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils, EventManager } from 'ng-jhipster';
import { OpengiveTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { SchoolDetailComponent } from '../../../../../../main/webapp/app/entities/school/school-detail.component';
import { SchoolService } from '../../../../../../main/webapp/app/entities/school/school.service';
import { School } from '../../../../../../main/webapp/app/entities/school/school.model';

describe('Component Tests', () => {

    describe('School Management Detail Component', () => {
        let comp: SchoolDetailComponent;
        let fixture: ComponentFixture<SchoolDetailComponent>;
        let service: SchoolService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [OpengiveTestModule],
                declarations: [SchoolDetailComponent],
                providers: [
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    SchoolService,
                    EventManager
                ]
            }).overrideComponent(SchoolDetailComponent, {
                set: {
                    template: ''
                }
            }).compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SchoolDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SchoolService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new School(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.school).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
