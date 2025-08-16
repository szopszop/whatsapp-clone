import { Component, OnInit } from '@angular/core';
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
export class AdminPanelComponent implements OnInit {
  grafanaDashboardUrl: SafeResourceUrl | undefined;
  constructor(private sanitizer: DomSanitizer) {}

  ngOnInit(): void {
    // Use the main Grafana URL to show the home page with all available dashboards
    const rawUrl = `${environment.grafanaUrl}`;
    this.grafanaDashboardUrl = this.sanitizer.bypassSecurityTrustResourceUrl(rawUrl);
  }
}
