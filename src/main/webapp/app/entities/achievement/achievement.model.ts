import { Activity } from '../activity';
import { User } from '../../shared';
export class Achievement {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public badgeUrl?: string,
        public activity?: Activity,
        public achievedBy?: User,
    ) {
    }
}
