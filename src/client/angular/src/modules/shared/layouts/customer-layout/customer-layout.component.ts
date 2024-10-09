import { Component } from '@angular/core';
import { HeaderComponent } from '../../common/header/header.component';
import { FooterComponent } from '../../common/footer/footer.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-customer-layout',
  standalone: true,
  imports: [HeaderComponent, FooterComponent, RouterModule],
  templateUrl: './customer-layout.component.html',
  styleUrl: './customer-layout.component.scss'
})
export class CustomerLayoutComponent {

}
