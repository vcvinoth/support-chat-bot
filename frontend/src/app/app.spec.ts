import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { App } from './app';

describe('App', () => {
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
  });

  it('renders the title and loads documents', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    http.expectOne('/api/documents').flush([]);
    await fixture.whenStable();

    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelector('h1')?.textContent).toContain('Support Chatbot');
    expect(element.textContent).toContain('No documents yet');
  });

  it('shows an error when documents cannot be loaded', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    http.expectOne('/api/documents').error(new ProgressEvent('error'), { status: 0 });
    await fixture.whenStable();

    expect(fixture.nativeElement.querySelector('[role=alert]')?.textContent).toContain(
      'Cannot reach the server',
    );
  });
});
