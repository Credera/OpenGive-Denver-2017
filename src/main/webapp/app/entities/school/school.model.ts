import { Address } from '../address';
import { Program } from '../program';
export class School {
    constructor(
        public id?: number,
        public name?: string,
        public district?: string,
        public address?: Address,
        public program?: Program,
    ) {
    }
}
