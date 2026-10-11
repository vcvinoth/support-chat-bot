import { Component, inject, signal } from '@angular/core';

import { ApiService, errorMessage } from '../core/api.service';
import { ChatInput } from './chat-input';
import { ChatMessageList } from './chat-message-list';
import { ChatMessage } from './chat.models';

/** Holds the conversation and sends questions to the backend. */
@Component({
  selector: 'app-chat',
  imports: [ChatMessageList, ChatInput],
  templateUrl: './chat.html',
  styleUrl: './chat.css',
})
export class Chat {
  private readonly api = inject(ApiService);
  private nextId = 0;

  protected readonly messages = signal<ChatMessage[]>([]);
  protected readonly waiting = signal(false);

  protected send(question: string): void {
    this.add('user', question);
    this.waiting.set(true);
    this.api.ask(question).subscribe({
      next: (response) => {
        this.add('assistant', response.answer, response.sources);
        this.waiting.set(false);
      },
      error: (error) => {
        this.add('error', errorMessage(error));
        this.waiting.set(false);
      },
    });
  }

  private add(role: ChatMessage['role'], text: string, sources: ChatMessage['sources'] = []): void {
    this.messages.update((list) => [...list, { id: this.nextId++, role, text, sources }]);
  }
}
