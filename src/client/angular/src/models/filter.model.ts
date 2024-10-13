export class FilterModel {
    public page!: number;
    public size!: number;
    public sort!: string;
    public direction!: SortDirection;
}

export enum SortDirection {
    ASC,
    DESC
}
