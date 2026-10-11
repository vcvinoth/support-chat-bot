import { Component, inject, output, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { ApiService, errorMessage } from '../core/api.service';

/** Lets the user pick one or more PDFs and uploads them one at a time. */
@Component({
  selector: 'app-document-upload',
  templateUrl: './document-upload.html',
  styleUrl: './document-upload.css',
})
export class DocumentUpload {
  private readonly api = inject(ApiService);

  /** Emitted after each upload attempt (success or failure) so the list can refresh. */
  readonly uploadFinished = output<void>();

  protected readonly uploading = signal<string | null>(null);
  protected readonly errors = signal<string[]>([]);

  protected async onFilesSelected(input: HTMLInputElement): Promise<void> {
    const files = Array.from(input.files ?? []);
    input.value = '';
    if (files.length === 0) {
      return;
    }
    this.errors.set([]);
    for (const file of files) {
      this.uploading.set(file.name);
      try {
        await firstValueFrom(this.api.uploadDocument(file));
      } catch (error) {
        this.errors.update((list) => [...list, `${file.name}: ${errorMessage(error)}`]);
      }
      this.uploadFinished.emit();
    }
    this.uploading.set(null);
  }
}
