import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { Chat } from './chat';

describe('Chat', () => {
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [Chat],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  async function ask(question: string) {
    const fixture = TestBed.createComponent(Chat);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    const textarea = element.querySelector('textarea') as HTMLTextAreaElement;
    textarea.value = question;
    textarea.dispatchEvent(new Event('input'));
    textarea.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', cancelable: true }));
    await fixture.whenStable();
    return { fixture, element };
  }

  it('shows the question, a pending state, then the answer with sources', async () => {
    const { fixture, element } = await ask('How do I log in?');

    expect(element.querySelector('.message.user')?.textContent).toContain('How do I log in?');
    expect(element.querySelector('.pending')).not.toBeNull();
    expect((element.querySelector('button') as HTMLButtonElement).disabled).toBe(true);

    const req = http.expectOne({ method: 'POST', url: '/api/chat' });
    expect(req.request.body).toEqual({ question: 'How do I log in?' });
    req.flush({
      answer: '1. Open the portal.\n2. Enter your user ID.',
      sources: [{ fileName: 'guide.pdf', page: 3 }],
    });
    await fixture.whenStable();

    expect(element.querySelector('.pending')).toBeNull();
    const answer = element.querySelector('.message.assistant');
    expect(answer?.textContent).toContain('Open the portal.');
    expect(answer?.querySelector('.sources')?.textContent).toContain('guide.pdf, page 3');
  });

  it('shows backend errors in the conversation', async () => {
    const { fixture, element } = await ask('hi');

    http
      .expectOne('/api/chat')
      .flush(
        { detail: 'The AI service is currently unavailable. Please try again later.' },
        { status: 502, statusText: 'Bad Gateway' },
      );
    await fixture.whenStable();

    expect(element.querySelector('.message.error [role=alert]')?.textContent).toContain(
      'The AI service is currently unavailable',
    );
    expect(element.querySelector('.pending')).toBeNull();
  });
});
