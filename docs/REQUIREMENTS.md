# Requirements

## Goal
Let a support user upload PDF documents and ask questions in a chat UI. Answers come from Claude, grounded in the uploaded content.

## Functional scope
1. **Upload PDFs** — upload one or more PDF files from the UI.
2. **Ingest** — extract text (PDFBox), split into chunks, embed, and store in the in-memory vector store.
3. **List documents** — show uploaded documents with basic status (indexed / failed). Re-uploading a file with the same name replaces the earlier copy.
4. **Chat** — type a question, see the answer in a conversation view.
5. **Grounded answers** — the answer uses only retrieved document chunks; it shows sources (file name, page).
6. **No-answer handling** — if nothing relevant is found, say so instead of guessing.
7. **Errors** — clear messages for invalid files, empty PDFs, and AI/API failures.

## Constraints
- In memory only: documents and vectors are lost on backend restart.
- Single user, local development use.
- Claude API key supplied via `ANTHROPIC_API_KEY` environment variable.
- Text-based PDFs only (no OCR for scanned images).

## Out of scope (for now)
Authentication, persistent storage/database, multi-tenancy, OCR, streaming responses, conversation memory across sessions, deployment/containers.
