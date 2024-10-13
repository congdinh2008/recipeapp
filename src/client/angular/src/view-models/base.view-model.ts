// Define the _links interface
export interface Link {
    rel: string;
    href: string;
}

// Define the Page interface
export interface Page {
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

// Define the BaseViewModel class
export class BaseViewModel<T> {
    public items!: T[];
    public links!: Link[];
    public page!: Page;
}