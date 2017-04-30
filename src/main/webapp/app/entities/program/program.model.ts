import { School } from '../school';
import { Course } from '../course';
export class Program {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public active?: boolean,
        public school?: School,
        public course?: Course,
    ) {
        this.active = false;
    }
}
