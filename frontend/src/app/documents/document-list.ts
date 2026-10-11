import { DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';

import { DocumentInfo } from '../core/api.models';

/** Shows uploaded documents and their indexing status. */
@Component({
  selector: 'app-document-list',
  imports: [DatePipe],
  templateUrl: './document-list.html',
  styleUrl: './document-list.css',
})
export class DocumentList {
  readonly documents = input.required<DocumentInfo[]>();
}
