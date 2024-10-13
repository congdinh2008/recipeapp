import { Component, EventEmitter, Inject, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { IRoleService } from '../../../../services/interfaces/role-service.interface';
import { ROLE_SERVICE } from '../../../../constants/injection.constant';
import { RoleViewModel } from '../../../../view-models/role/role.view-model';

@Component({
  selector: 'app-role-detail',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './role-detail.component.html',
  styleUrl: './role-detail.component.scss'
})
export class RoleDetailComponent implements OnInit {
  public data: RoleViewModel | null = null;
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
    @Inject(ROLE_SERVICE) private roleService: IRoleService
  ) { }

  ngOnInit(): void {
    this.createForm();
  }

  private createForm() {

    this.form = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.maxLength(255), Validators.minLength(3)]),
      description: new FormControl('', Validators.maxLength(500))
    });
  }

  private getData() {
    this.roleService.getById(this.selectedId).subscribe((data) => {
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
      this.roleService.update(data).subscribe(() => {
        this.onSave.emit(true);
      });
    } else {
      this.roleService.create(data).subscribe(() => {
        this.onSave.emit(true);
      });
    }
  }

  public onCancel(): void {
    this.onClose.emit(true);
  }

  public onReset(): void {
    if (this.selectedId) {
      this.roleService.getById(this.selectedId).subscribe((data) => {
        this.form.patchValue(data);
      });
    } else {
      this.form.reset();
    }
  }
}
