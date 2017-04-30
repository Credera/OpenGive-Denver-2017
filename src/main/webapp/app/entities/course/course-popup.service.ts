import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { DatePipe } from '@angular/common';
import { Course } from './course.model';
import { CourseService } from './course.service';
@Injectable()
export class CoursePopupService {
    private isOpen = false;
    constructor(
        private datePipe: DatePipe,
        private modalService: NgbModal,
        private router: Router,
        private courseService: CourseService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.courseService.find(id).subscribe((course) => {
                course.startDate = this.datePipe
                    .transform(course.startDate, 'yyyy-MM-ddThh:mm');
                course.endDate = this.datePipe
                    .transform(course.endDate, 'yyyy-MM-ddThh:mm');
                this.courseModalRef(component, course);
            });
        } else {
            return this.courseModalRef(component, new Course());
        }
    }

    courseModalRef(component: Component, course: Course): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.course = course;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
