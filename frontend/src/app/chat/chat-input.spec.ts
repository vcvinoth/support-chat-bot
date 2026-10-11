import { TestBed } from '@angular/core/testing';

import { ChatInput } from './chat-input';

describe('ChatInput', () => {
  function setup(disabled = false) {
    const fixture = TestBed.createComponent(ChatInput);
    fixture.componentRef.setInput('disabled', disabled);
    const sent: string[] = [];
    fixture.componentInstance.send.subscribe((q) => sent.push(q));
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;
    const textarea = element.querySelector('textarea') as HTMLTextAreaElement;
    const button = element.querySelector('button') as HTMLButtonElement;
    const type = (value: string) => {
      textarea.value = value;
      textarea.dispatchEvent(new Event('input'));
      fixture.detectChanges();
    };
    const press = (key: string, shiftKey = false) => {
      textarea.dispatchEvent(new KeyboardEvent('keydown', { key, shiftKey, cancelable: true }));
      fixture.detectChanges();
    };
    return { fixture, textarea, button, sent, type, press };
  }

  it('sends the trimmed question on Enter and clears the box', () => {
    const { textarea, sent, type, press } = setup();
    type('  How do I log in?  ');
    press('Enter');

    expect(sent).toEqual(['How do I log in?']);
    expect(textarea.value).toBe('');
  });

  it('clears the box even when Enter follows input without a render in between', () => {
    const { fixture, textarea, sent } = setup();
    textarea.value = 'Pasted question';
    textarea.dispatchEvent(new Event('input'));
    textarea.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', cancelable: true }));
    fixture.detectChanges();

    expect(sent).toEqual(['Pasted question']);
    expect(textarea.value).toBe('');
  });

  it('does not send on Shift+Enter', () => {
    const { sent, type, press } = setup();
    type('line one');
    press('Enter', true);

    expect(sent).toEqual([]);
  });

  it('disables Send for a blank question', () => {
    const { button, sent, type, press } = setup();
    type('   ');
    press('Enter');

    expect(button.disabled).toBe(true);
    expect(sent).toEqual([]);
  });

  it('does not send while disabled', () => {
    const { button, sent, type, press } = setup(true);
    type('question');
    press('Enter');

    expect(button.disabled).toBe(true);
    expect(sent).toEqual([]);
  });
});
