import { Component, OnInit, inject, signal } from '@angular/core';

import { Chat } from './chat/chat';
import { DocumentInfo } from './core/api.models';
import { ApiService, errorMessage } from './core/api.service';
import { DocumentList } from './documents/document-list';
import { DocumentUpload } from './documents/document-upload';

@Component({
  selector: 'app-root',
  imports: [DocumentUpload, DocumentList, Chat],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly title = signal('Support Chatbot');
  protected readonly documents = signal<DocumentInfo[]>([]);
  protected readonly loadError = signal<string | null>(null);

  ngOnInit(): void {
    this.loadDocuments();
  }

  protected loadDocuments(): void {
    this.api.listDocuments().subscribe({
      next: (documents) => {
        this.documents.set(documents);
        this.loadError.set(null);
      },
      error: (error) => this.loadError.set(errorMessage(error)),
    });
  }
}
