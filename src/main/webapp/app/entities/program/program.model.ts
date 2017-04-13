import { Organization } from '../organization';
import { User } from '../../shared';
import { ItemLink } from '../item-link';
import { Milestone } from '../milestone';
export class Program {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public startDate?: any,
        public endDate?: any,
        public organization?: Organization,
        public instructor?: User,
        public resources?: ItemLink,
        public students?: User,
        public milestones?: Milestone,
    ) {
    }
}
