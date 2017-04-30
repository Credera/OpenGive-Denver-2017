import { Portfolio } from '../portfolio';
import { Course } from '../course';
export class ItemLink {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public thumbnailImageUrl?: string,
        public itemUrl?: string,
        public portfolio?: Portfolio,
        public course?: Course,
    ) {
    }
}
