import {Component, Inject, OnInit} from "@angular/core";
import {MdDialogRef, MD_DIALOG_DATA} from "@angular/material";

@Component({
  selector: 'admin-dialog',
  template: `
    <div class="admin-dialog" [ngSwitch]="data.tab">
      <admin-organizations-form *ngSwitchCase="'organizations'" [item]="data.item" [adding]="data.adding"></admin-organizations-form>
      <!--<admin-administrators-form *ngSwitchCase="'administrators'" [item]="copy" [editing]="editing" [adding]="adding"></admin-administrators-form>-->
      <!--<admin-instructors-form *ngSwitchCase="'instructors'" [item]="copy" [editing]="editing" [adding]="adding"></admin-instructors-form>-->
      <!--<admin-students-form *ngSwitchCase="'students'" [item]="copy" [editing]="editing" [adding]="adding"></admin-students-form>-->
      <!--<admin-sessions-form *ngSwitchCase="'sessions'" [item]="copy" [editing]="editing"></admin-sessions-form>-->
      <!--<admin-programs-form *ngSwitchCase="'programs'" [item]="copy" [editing]="editing"></admin-programs-form>-->
      <!--<admin-courses-form *ngSwitchCase="'courses'" [item]="copy" [editing]="editing"></admin-courses-form>-->
    </div>`
})
export class AdminDialogComponent implements OnInit {

  constructor(@Inject(MD_DIALOG_DATA) public data: any) {}

  ngOnInit(): void {}
}