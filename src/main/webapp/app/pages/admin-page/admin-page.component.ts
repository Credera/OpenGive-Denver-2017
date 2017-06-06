import {Component, OnInit} from "@angular/core";
import {AdminModel} from "../../controls/admin/admin.constants";
import {Router} from "@angular/router";

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.css']
})
export class AdminPageComponent implements OnInit {

  tabs = [
    { name: AdminModel.Organization.title, route: AdminModel.Organization.route, active: false },
    { name: AdminModel.Administrator.title, route: AdminModel.Administrator.route, active: false },
    { name: AdminModel.Instructor.title, route: AdminModel.Instructor.route, active: false },
    { name: AdminModel.Student.title, route: AdminModel.Student.route, active: false },
    { name: AdminModel.Session.title, route: AdminModel.Session.route, active: false },
    { name: AdminModel.Program.title, route: AdminModel.Program.route, active: false },
    { name: AdminModel.Course.title, route: AdminModel.Course.route, active: false },
  ];
  activeTab = this.tabs[0];

  adminPageFlex = {
    lg: '1280px',
    xs: '100%'
  };

  constructor(private router: Router) {}

  ngOnInit() { // Highlight the correct tab using the url
    this.selectTab(this.tabs.find(tab => tab.route === this.router.url.split('/').pop()));
  }

  selectTab(tab) {
    this.tabs.forEach(tab => tab.active = false);
    tab.active = true;
    this.activeTab = tab;
  }

}