import { Course } from '../course';
import { Achievement } from '../achievement';
export class Activity {
    constructor(
        public id?: number,
        public name?: string,
        public course?: Course,
        public achievement?: Achievement,
    ) {
    }
}
