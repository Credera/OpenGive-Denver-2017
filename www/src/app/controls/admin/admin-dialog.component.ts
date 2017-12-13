import {Component, Inject} from "@angular/core";
import {MD_DIALOG_DATA} from "@angular/material";

@Component({
  selector: 'admin-dialog',
  template: `
    <div class="admin-dialog" [ngSwitch]="data.tab">
      <admin-administrators-dialog *ngSwitchCase="'administrators'" [item]="data.item" [adding]="data.adding" [organizations]="data.organizations"></admin-administrators-dialog>
      <admin-org-administrators-form *ngSwitchCase="'org-administrators'" [item]="data.item" [adding]="data.adding" [organizations]="data.organizations"></admin-org-administrators-form>
      <admin-instructors-form *ngSwitchCase="'instructors'" [item]="data.item" [adding]="data.adding" [organizations]="data.organizations"></admin-instructors-form>
      <admin-students-form *ngSwitchCase="'students'" [item]="data.item" [adding]="data.adding" [organizations]="data.organizations"></admin-students-form>
      <admin-organizations-form *ngSwitchCase="'organizations'" [item]="data.item" [adding]="data.adding"></admin-organizations-form>
      <admin-sessions-form *ngSwitchCase="'sessions'" [item]="data.item" [adding]="data.adding"></admin-sessions-form>
      <admin-programs-form *ngSwitchCase="'programs'" [item]="data.item" [adding]="data.adding" [organizations]="data.organizations"></admin-programs-form>
      <admin-courses-form *ngSwitchCase="'courses'" [item]="data.item" [adding]="data.adding"></admin-courses-form>
    </div>`
})
export class AdminDialogComponent {

  constructor(@Inject(MD_DIALOG_DATA) public data: any) {}
}
