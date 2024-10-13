import { Component, Inject, OnInit } from '@angular/core';
import { ROLE_SERVICE } from '../../../constants/injection.constant';
import { FilterModel, SortDirection } from '../../../models/filter.model';
import { IRoleService } from '../../../services/interfaces/role-service.interface';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RoleSearchViewModel } from '../../../view-models/role/role-search.view-model';
import { FontAwesomeModule, IconDefinition } from '@fortawesome/angular-fontawesome';
import { faAngleDoubleLeft, faAngleDoubleRight, faAngleLeft, faAngleRight, faEdit, faTrash } from '@fortawesome/free-solid-svg-icons';
import { RoleDetailComponent } from './role-detail/role-detail.component';

@Component({
  selector: 'app-role',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FontAwesomeModule, RoleDetailComponent],
  templateUrl: './role.component.html',
  styleUrl: './role.component.scss'
})
export class RoleComponent implements OnInit {

  public faEdit: IconDefinition = faEdit;
  public faTrash: IconDefinition = faTrash;
  public faAngleLeft: IconDefinition = faAngleLeft;
  public faAngleDoubleLeft: IconDefinition = faAngleDoubleLeft;
  public faAngleRight: IconDefinition = faAngleRight;
  public faAngleDoubleRight: IconDefinition = faAngleDoubleRight;

  public data!: RoleSearchViewModel;
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
    @Inject(ROLE_SERVICE) private roleService: IRoleService
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
    const roleFilter = Object.assign({}, this.filter, {
      keyword: this.form.get('keyword')?.value
    });
    this.roleService.search(roleFilter).subscribe((data: RoleSearchViewModel) => {
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
    this.roleService.delete(id).subscribe((data) => {
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
