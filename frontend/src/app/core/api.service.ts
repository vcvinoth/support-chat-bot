import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ChatAnswer, DocumentInfo, ProblemDetail } from './api.models';

/** Single entry point for all backend calls. */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  listDocuments(): Observable<DocumentInfo[]> {
    return this.http.get<DocumentInfo[]>('/api/documents');
  }

  uploadDocument(file: File): Observable<DocumentInfo> {
    const body = new FormData();
    body.append('file', file);
    return this.http.post<DocumentInfo>('/api/documents', body);
  }

  ask(question: string): Observable<ChatAnswer> {
    return this.http.post<ChatAnswer>('/api/chat', { question });
  }
}

/** Turns an HTTP error into a message suitable for showing to the user. */
export function errorMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return 'Cannot reach the server. Is the backend running?';
    }
    const problem = error.error as ProblemDetail | null;
    if (problem?.detail) {
      return problem.detail;
    }
    return `Request failed (${error.status}).`;
  }
  return 'Something went wrong. Please try again.';
}
