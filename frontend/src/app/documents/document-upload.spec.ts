import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { DocumentInfo } from '../core/api.models';
import { ApiService } from '../core/api.service';
import { DocumentUpload } from './document-upload';

describe('DocumentUpload', () => {
  const uploadDocument = vi.fn();

  beforeEach(() => {
    uploadDocument.mockReset();
    TestBed.configureTestingModule({
      imports: [DocumentUpload],
      providers: [{ provide: ApiService, useValue: { uploadDocument } }],
    });
  });

  function selectFiles(
    fixture: ReturnType<typeof TestBed.createComponent<DocumentUpload>>,
    files: File[],
  ) {
    const input = fixture.nativeElement.querySelector('input[type=file]') as HTMLInputElement;
    Object.defineProperty(input, 'files', { value: files, configurable: true });
    input.dispatchEvent(new Event('change'));
  }

  it('uploads each selected file and notifies after each one', async () => {
    uploadDocument.mockReturnValue(of({} as DocumentInfo));
    const fixture = TestBed.createComponent(DocumentUpload);
    let finished = 0;
    fixture.componentInstance.uploadFinished.subscribe(() => finished++);
    await fixture.whenStable();

    selectFiles(fixture, [new File(['a'], 'a.pdf'), new File(['b'], 'b.pdf')]);
    await fixture.whenStable();

    expect(uploadDocument).toHaveBeenCalledTimes(2);
    expect(finished).toBe(2);
    expect(fixture.nativeElement.querySelector('.error')).toBeNull();
  });

  it('shows the backend error for a rejected file', async () => {
    uploadDocument.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 400,
            error: { detail: 'Only PDF files are supported.' },
          }),
      ),
    );
    const fixture = TestBed.createComponent(DocumentUpload);
    await fixture.whenStable();

    selectFiles(fixture, [new File(['x'], 'notes.pdf')]);
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.error')?.textContent).toContain(
      'notes.pdf: Only PDF files are supported.',
    );
  });
});
