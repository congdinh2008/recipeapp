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
import { IUserService } from '../../../../services/interfaces/user-service.interface';
import { USER_SERVICE } from '../../../../constants/injection.constant';
import { UserViewModel } from '../../../../view-models/user/user.view-model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './user-detail.component.html',
  styleUrl: './user-detail.component.scss',
})
export class UserDetailComponent implements OnInit {
  public data: UserViewModel | null = null;
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

  constructor(@Inject(USER_SERVICE) private userService: IUserService) {}

  ngOnInit(): void {
    this.createForm();
  }

  private createForm() {
    this.form = new FormGroup({
      firstName: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
        Validators.minLength(3),
      ]),
      lastName: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
        Validators.minLength(3),
      ]),
      email: new FormControl('', [Validators.required, Validators.email]),
      username: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
        Validators.minLength(3),
      ]),
    });
  }

  private getData() {
    this.userService.getById(this.selectedId).subscribe((data) => {
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
      this.userService.update(data).subscribe(() => {
        this.onSave.emit(true);
      });
    } else {
      this.userService.create(data).subscribe(() => {
        this.onSave.emit(true);
      });
    }
  }

  public onCancel(): void {
    this.onClose.emit(true);
  }

  public onReset(): void {
    if (this.selectedId) {
      this.userService.getById(this.selectedId).subscribe((data) => {
        this.form.patchValue(data);
      });
    } else {
      this.form.reset();
    }
  }
}
