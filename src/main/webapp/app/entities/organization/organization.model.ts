import { Course } from '../course';
import { User } from '../../shared';
export class Organization {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public courses?: Course,
        public users?: User,
    ) {
    }
}
