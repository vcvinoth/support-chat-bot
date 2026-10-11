import { TestBed } from '@angular/core/testing';

import { DocumentInfo } from '../core/api.models';
import { DocumentList } from './document-list';

describe('DocumentList', () => {
  function render(documents: DocumentInfo[]): HTMLElement {
    const fixture = TestBed.createComponent(DocumentList);
    fixture.componentRef.setInput('documents', documents);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('shows an empty state', () => {
    expect(render([]).textContent).toContain('No documents yet');
  });

  it('shows indexed and failed documents', () => {
    const element = render([
      {
        id: '1',
        fileName: 'guide.pdf',
        status: 'INDEXED',
        pages: 20,
        chunks: 21,
        error: null,
        uploadedAt: '2026-10-11T06:00:00Z',
      },
      {
        id: '2',
        fileName: 'scan.pdf',
        status: 'FAILED',
        pages: 0,
        chunks: 0,
        error: 'No extractable text',
        uploadedAt: '2026-10-11T06:01:00Z',
      },
    ]);

    const items = element.querySelectorAll('li');
    expect(items.length).toBe(2);
    expect(items[0].textContent).toContain('guide.pdf');
    expect(items[0].textContent).toContain('20 pages · 21 chunks');
    expect(items[1].querySelector('.badge.failed')?.textContent).toContain('Failed');
    expect(items[1].textContent).toContain('No extractable text');
  });
});
