import { Course } from '../course';
import { Organization } from '../organization';
export class Session {
    constructor(
        public id?: number,
        public name?: string,
        public active?: boolean,
        public manytomany?: Course,
        public organization?: Organization,
    ) {
        this.active = false;
    }
}
