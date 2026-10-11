import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ApiService, errorMessage } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lists documents', () => {
    let count = -1;
    service.listDocuments().subscribe((docs) => (count = docs.length));

    http.expectOne({ method: 'GET', url: '/api/documents' }).flush([]);

    expect(count).toBe(0);
  });

  it('uploads a file as multipart form data', () => {
    const file = new File(['%PDF-1.7'], 'guide.pdf', { type: 'application/pdf' });
    service.uploadDocument(file).subscribe();

    const req = http.expectOne({ method: 'POST', url: '/api/documents' });
    expect(req.request.body instanceof FormData).toBe(true);
    expect((req.request.body as FormData).get('file')).toBeInstanceOf(File);
    req.flush({});
  });
});

describe('errorMessage', () => {
  it('uses the problem detail from the backend', () => {
    const error = new HttpErrorResponse({
      status: 400,
      error: { detail: 'Only PDF files are supported.' },
    });
    expect(errorMessage(error)).toBe('Only PDF files are supported.');
  });

  it('explains when the server is unreachable', () => {
    expect(errorMessage(new HttpErrorResponse({ status: 0 }))).toContain('Cannot reach the server');
  });

  it('falls back to the status code', () => {
    expect(errorMessage(new HttpErrorResponse({ status: 500 }))).toBe('Request failed (500).');
  });
});
