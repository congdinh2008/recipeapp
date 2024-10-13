import {
  Component,
  EventEmitter,
  Inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { IRecipeService } from '../../../../services/interfaces/recipe-service.interface';
import {
  CATEGORY_SERVICE,
  RECIPE_SERVICE,
} from '../../../../constants/injection.constant';
import { RecipeViewModel } from '../../../../view-models/recipe/recipe.view-model';
import { CategoryViewModel } from '../../../../view-models/category/category.view-model';
import { ICategoryService } from '../../../../services/interfaces/category-service.interface';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-recipe-detail',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.scss',
})
export class RecipeDetailComponent implements OnInit {
  public data: RecipeViewModel | null = null;
  public categories: CategoryViewModel[] = [];
  private _selectedId!: string;
  @Input('id')
  set selectedId(value: string) {
    this._selectedId = value;
    if (this._selectedId) {
      this.getData();
    } else {
      if (this.form) {
        this.form.reset();
      }
      this.data = null;
    }
  }

  public get selectedId(): string {
    return this._selectedId;
  }
  @Output() onClose: EventEmitter<any> = new EventEmitter();
  @Output() onSave: EventEmitter<any> = new EventEmitter();

  public form!: FormGroup;

  constructor(
    @Inject(RECIPE_SERVICE) private recipeService: IRecipeService,
    @Inject(CATEGORY_SERVICE) private categoryService: ICategoryService
  ) {}

  ngOnInit(): void {
    this.createForm();
    this.getCategories();
  }

  private getCategories() {
    this.categoryService.getAll().subscribe((data) => {
      this.categories = data;
    });
  }

  private createForm() {
    this.form = new FormGroup({
      title: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
        Validators.minLength(3),
      ]),
      prepTime: new FormControl(0, [Validators.required, Validators.min(0)]),
      cookTime: new FormControl(0, [Validators.required, Validators.min(0)]),
      servings: new FormControl(0, [Validators.required, Validators.min(0)]),
      description: new FormControl(''),
      categoryId: new FormControl(null, [Validators.required]),
    });
  }

  private getData() {
    this.recipeService.getById(this.selectedId).subscribe((data) => {
      this.data = data;
      this.form.patchValue(data);
    });
  }

  public onSubmit(): void {
    if (this.form.invalid) {
      return;
    }

    const data = this.form.value;
    if (this.selectedId) {
      data.id = this.selectedId;
      this.recipeService.update(data).subscribe(() => {
        this.onSave.emit(true);
      });
    } else {
      this.recipeService.create(data).subscribe(() => {
        this.onSave.emit(true);
      });
    }
  }

  public onCancel(): void {
    this.onClose.emit(true);
  }

  public onReset(): void {
    if (this.selectedId) {
      this.recipeService.getById(this.selectedId).subscribe((data) => {
        this.form.patchValue(data);
      });
    } else {
      this.form.reset();
    }
  }
}
