import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule, IconDefinition } from '@fortawesome/angular-fontawesome';
import { faCarrot, faGaugeHigh, faList, faUser, faUserShield, faUtensils } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, FontAwesomeModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  public faGaugeHigh: IconDefinition = faGaugeHigh;
  public faList: IconDefinition = faList;
  public faCarrot: IconDefinition = faCarrot;
  public faUtensils: IconDefinition = faUtensils;
  public faUser: IconDefinition = faUser;
  public faUserShield: IconDefinition = faUserShield;

}
