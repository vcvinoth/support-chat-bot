# Progress

## Done
- [x] Project structure, CLAUDE.md, docs, and skills created (2026-10-11).
- [x] Backend skeleton: Spring Boot 4.1.1, Java 21, Spring AI 2.0.1 BOM, web + validation; context test passes (2026-10-11).
- [x] Frontend skeleton: Angular 21.2, CSS, no SSR/router, `/api` dev proxy to :8080; build + tests pass (2026-10-11).

- [x] Backend ingestion: `POST/GET /api/documents`, PDFBox → 200-token chunks with page metadata → local ONNX embeddings → SimpleVectorStore; 12 tests pass; sample PDF indexed (20 pages, 21 chunks) (2026-10-11).

- [x] Backend chat: `POST /api/chat` (top-5 retrieval, 0.35 threshold → Claude `claude-opus-5` → answer + sources); 19 tests pass; retrieval verified live on sample PDF. **Live Claude answer not yet verified — needs `ANTHROPIC_API_KEY`** (2026-10-11).

- [x] Frontend documents panel: `ApiService` (`core/`), `DocumentUpload` (multi-file, progress, per-file errors) and `DocumentList` (status badge, pages/chunks) in `documents/`; 11 tests pass; verified in Chrome via dev proxy (2026-10-11).

- [x] Frontend chat UI: `Chat` (state + API), `ChatMessageList` (auto-scroll, sources, pending, error bubbles), `ChatInput` (Enter/Shift+Enter, disabled while waiting); two-column layout, stacks under 760px; 17 frontend tests pass; not-found and error paths verified in Chrome. Backend prompt now asks for plain text (2026-10-11).

- [x] Re-upload with the same file name replaces the earlier copy and its chunks (a failed re-upload keeps the indexed copy); 23 backend tests pass; verified live (2026-10-11).

- [x] Narrow-screen check at 390px: fixed chat height on mobile so messages scroll inside and the input stays visible; question box now always clears after send (regression test added); 18 frontend tests pass (2026-10-11).

## Next task
- [ ] Set `ANTHROPIC_API_KEY`, run both apps, verify real Claude answers in the UI for the sample PDF.

## Backlog
- [ ] Error handling and tests
