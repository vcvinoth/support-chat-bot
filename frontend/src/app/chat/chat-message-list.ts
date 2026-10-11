import { Component, ElementRef, effect, inject, input } from '@angular/core';

import { ChatMessage } from './chat.models';

/** Scrollable conversation view; keeps the newest message in view. */
@Component({
  selector: 'app-chat-message-list',
  templateUrl: './chat-message-list.html',
  styleUrl: './chat-message-list.css',
})
export class ChatMessageList {
  private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);

  readonly messages = input.required<ChatMessage[]>();
  readonly waiting = input(false);

  constructor() {
    effect(() => {
      this.messages();
      this.waiting();
      queueMicrotask(() => {
        const el = this.host.nativeElement;
        el.scrollTop = el.scrollHeight;
      });
    });
  }
}
