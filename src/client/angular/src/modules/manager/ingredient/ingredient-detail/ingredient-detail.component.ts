import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { IIngredientService } from '../../../../services/interfaces/ingredient-service.interface';
import { INGREDIENT_SERVICE } from '../../../../constants/injection.constant';
import { IngredientViewModel } from '../../../../view-models/ingredient/ingredient.view-model';

@Component({
  selector: 'app-ingredient-detail',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './ingredient-detail.component.html',
  styleUrl: './ingredient-detail.component.scss'
})
export class IngredientDetailComponent implements OnInit {
  public data: IngredientViewModel | null = null;
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
    @Inject(INGREDIENT_SERVICE) private ingredientService: IIngredientService
  ) { }

  ngOnInit(): void {
    this.createForm();
  }

  private createForm() {

    this.form = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.maxLength(255), Validators.minLength(3)]),
    });
  }

  private getData() {
    this.ingredientService.getById(this.selectedId).subscribe((data) => {
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
      this.ingredientService.update(data).subscribe(() => {
        this.onSave.emit(true);
      });
    } else {
      this.ingredientService.create(data).subscribe(() => {
        this.onSave.emit(true);
      });
    }
  }

  public onCancel(): void {
    this.onClose.emit(true);
  }

  public onReset(): void {
    if (this.selectedId) {
      this.ingredientService.getById(this.selectedId).subscribe((data) => {
        this.form.patchValue(data);
      });
    } else {
      this.form.reset();
    }
  }
}
