import { Component, Inject, OnInit } from '@angular/core';
import { INGREDIENT_SERVICE } from '../../../constants/injection.constant';
import { FilterModel, SortDirection } from '../../../models/filter.model';
import { IIngredientService } from '../../../services/interfaces/ingredient-service.interface';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { IngredientSearchViewModel } from '../../../view-models/ingredient/ingredient-search.view-model';
import { FontAwesomeModule, IconDefinition } from '@fortawesome/angular-fontawesome';
import { faAngleDoubleLeft, faAngleDoubleRight, faAngleLeft, faAngleRight, faEdit, faTrash } from '@fortawesome/free-solid-svg-icons';
import { IngredientDetailComponent } from './ingredient-detail/ingredient-detail.component';

@Component({
  selector: 'app-ingredient',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FontAwesomeModule, IngredientDetailComponent],
  templateUrl: './ingredient.component.html',
  styleUrl: './ingredient.component.scss'
})
export class IngredientComponent implements OnInit {

  public faEdit: IconDefinition = faEdit;
  public faTrash: IconDefinition = faTrash;
  public faAngleLeft: IconDefinition = faAngleLeft;
  public faAngleDoubleLeft: IconDefinition = faAngleDoubleLeft;
  public faAngleRight: IconDefinition = faAngleRight;
  public faAngleDoubleRight: IconDefinition = faAngleDoubleRight;

  public data!: IngredientSearchViewModel;
  public form!: FormGroup;
  public isShowDetail: boolean = false;
  public selectedId: string = '';

  private filter: FilterModel = {
    page: 0,
    size: 10,
    sort: 'name',
    direction: SortDirection.ASC,
  }

  public pageSizes: number[] = [5, 10, 20, 50];
  public currentPageSize: number = this.pageSizes[1];
  public currentPage: number = 0;
  public pageLimit: number = 3;

  constructor(
    @Inject(INGREDIENT_SERVICE) private ingredientService: IIngredientService
  ) { }


  ngOnInit(): void {
    this.createForm();
    this.getData();
  }

  private createForm() {
    this.form = new FormGroup({
      keyword: new FormControl('')
    });
  }

  private getData(): void {
    const ingredientFilter = Object.assign({}, this.filter, {
      keyword: this.form.get('keyword')?.value
    });
    this.ingredientService.search(ingredientFilter).subscribe((data: IngredientSearchViewModel) => {
      if (data) {
        this.data = data;
      }
    });
  }

  public onSearch(): void {
    this.getData();
  }

  public onReset(): void {
    this.form.reset();
    this.getData();
  }

  public onCreate(): void {
    this.isShowDetail = true;
    this.selectedId = '';
  }

  public onCancel(event: any): void {
    this.isShowDetail = false;
    this.selectedId = '';
  }

  public onSave(event: any): void {
    this.getData();
    this.isShowDetail = false;
    this.selectedId = '';
  }

  public onEdit(id: string): void {
    this.onCancel(id);
    this.selectedId = id;
    this.isShowDetail = true;
  }

  public onDelete(id: string): void {
    this.ingredientService.delete(id).subscribe((data) => {
      if (data) {
        this.getData();
      } else {
        alert('Delete failed');
      }
    });
  }

  public onPageSize($event: Event) {
    this.filter.size = Number(($event.target as HTMLSelectElement).value);
    this.getData();
  }

  public getPageSequence(currentPage: number, pageLimit: number, totalPages: number): number[] {
    const start = Math.max(0, currentPage - pageLimit);
    const end = Math.min(totalPages - 1, currentPage + pageLimit);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }

  public onChangePageNumber(pageNumber: number) {
    this.filter.page = pageNumber;
    this.currentPage = pageNumber;
    this.getData();
  }
}
