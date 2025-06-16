import { Component } from '@angular/core';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {environment} from '../../../environments/environment';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-admin',
  imports: [
    NgIf
  ],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.scss'
})
export class AdminPanelComponent {
  grafanaDashboardUrl: SafeResourceUrl | undefined;
  constructor(private sanitizer: DomSanitizer) {}

  ngOnInit(): void {

    const rawUrl = `${environment.grafanaUrl}/d-solo/your-dashboard-uid/your-dashboard-name?orgId=1&panelId=2&theme=light`;
    this.grafanaDashboardUrl = this.sanitizer.bypassSecurityTrustResourceUrl(rawUrl);
  }
}
