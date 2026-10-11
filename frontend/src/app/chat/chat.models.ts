import { ChatSource } from '../core/api.models';

/** One entry in the conversation shown to the user. */
export interface ChatMessage {
  id: number;
  role: 'user' | 'assistant' | 'error';
  text: string;
  sources: ChatSource[];
}
