import { Component, ElementRef, computed, input, output, signal, viewChild } from '@angular/core';

/** Question box: Enter sends, Shift+Enter adds a new line. */
@Component({
  selector: 'app-chat-input',
  templateUrl: './chat-input.html',
  styleUrl: './chat-input.css',
})
export class ChatInput {
  /** Matches the backend limit on question length. */
  protected readonly maxLength = 2000;

  readonly disabled = input(false);
  readonly send = output<string>();

  private readonly box = viewChild.required<ElementRef<HTMLTextAreaElement>>('box');

  protected readonly text = signal('');
  protected readonly canSend = computed(() => !this.disabled() && this.text().trim().length > 0);

  protected onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey && !event.isComposing) {
      event.preventDefault();
      this.submit();
    }
  }

  protected submit(): void {
    if (!this.canSend()) {
      return;
    }
    this.send.emit(this.text().trim());
    this.text.set('');
    // Clear the element directly: if no render happened since the last keystroke, the [value]
    // binding still holds '' and would not update the DOM.
    this.box().nativeElement.value = '';
  }
}
