---
name: chatbot-ui
description: Conventions for building the Angular 21 chat and document-upload UI in frontend/. Use when creating or changing Angular components, services, styles, or frontend tests.
---

# Chatbot UI (Angular 21)

## Before coding
- Read `docs/ARCHITECTURE.md` for the API contract.
- Check `frontend/package.json` for the installed Angular version; use only APIs it provides.

## Conventions
- Standalone components, signals for state, `inject()` for DI, new control flow (`@if`, `@for`).
- One `ApiService` wraps all `HttpClient` calls to `/api/...`; components never call HttpClient directly.
- Typed interfaces for every request/response (mirror backend DTOs).
- Plain CSS; no UI/component library or state library without approval.
- Keep components small: upload, document list, message list, chat input.

## UX requirements
- Show loading state while uploading and while waiting for an answer.
- Disable send when input is empty or a request is in flight.
- Show answer sources (file name, page) under each bot message.
- Show readable error messages; never a blank screen.
- Basic accessibility: labels on inputs, keyboard send (Enter).

## Done when
- `npm run build` and `npm test` pass.
- Run `change-explainer` and update `docs/PROGRESS.md`.
