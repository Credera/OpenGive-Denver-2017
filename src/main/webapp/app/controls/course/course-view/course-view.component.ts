import { Course } from './../../../models/course';
import { CourseService } from './../../../services/course.service';
import {Component, Input, OnInit, Inject} from '@angular/core';
import {MD_DIALOG_DATA} from '@angular/material';

@Component({
  selector: 'app-course-view',
  templateUrl: './course-view.component.html',
  styleUrls: ['./course-view.component.css']
})
export class CourseViewComponent implements OnInit {

  constructor(@Inject(MD_DIALOG_DATA) public course: Course, private courseService: CourseService) {
  }

  ngOnInit() {
  //reload the course to retrieve the list of activities
    this.courseService.get(this.course.id).subscribe(course => {this.course = course});
  }

  calculateTotalPoints(): Number {
    if (!this.course.activities) return 0;
    return this.course.activities.reduce(function(a,b) { return a + b.points; }, 0);
  }

}