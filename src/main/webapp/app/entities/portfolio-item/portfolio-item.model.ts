import { Portfolio } from '../portfolio';
import { Course } from '../course';
import { ItemLink } from '../item-link';
export class PortfolioItem {
    constructor(
        public id?: number,
        public portfolio?: Portfolio,
        public course?: Course,
        public resource?: ItemLink,
    ) {
    }
}
