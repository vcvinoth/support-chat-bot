/** Mirrors backend `DocumentInfo`. */
export interface DocumentInfo {
  id: string;
  fileName: string;
  status: 'INDEXED' | 'FAILED';
  pages: number;
  chunks: number;
  error: string | null;
  uploadedAt: string;
}

/** RFC 9457 problem detail returned by the backend on errors. */
export interface ProblemDetail {
  title?: string;
  status?: number;
  detail?: string;
}

/** Mirrors backend `ChatService.Source`. */
export interface ChatSource {
  fileName: string;
  page: number;
}

/** Mirrors backend `ChatService.Answer`. */
export interface ChatAnswer {
  answer: string;
  sources: ChatSource[];
}
