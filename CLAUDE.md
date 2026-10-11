# Support AI Chatbot

A support chatbot that answers questions using uploaded PDF documents (RAG) and Claude.

## Stack
- **Frontend** (`frontend/`): Angular 21, standalone components, signals.
- **Backend** (`backend/`): Java 21, Spring Boot 4, Spring AI (Anthropic chat, Transformers/ONNX embeddings, `SimpleVectorStore`), Apache PDFBox.
- No database, no Docker, no extra infrastructure. All indexed data is in memory.

## Layout
```
frontend/   Angular app (proxy.conf.json forwards /api → :8080)
backend/    Spring Boot app (package com.example.supportchat)
docs/       REQUIREMENTS.md, ARCHITECTURE.md, PROGRESS.md
.claude/skills/  chatbot-ui, springboot-api, rag-documents, change-explainer
```

## Commands
- Backend: `cd backend && ./mvnw spring-boot:run` (port 8080) · tests: `./mvnw test`
- Frontend: `cd frontend && npm start` (port 4200) · build: `npm run build` · tests: `npm test -- --watch=false`
- Required env var: `ANTHROPIC_API_KEY` (never commit it).

## Rules
1. Read `docs/PROGRESS.md` first; work only on the listed next task.
2. Ask before adding any dependency, framework or infrastructure.
3. Use stable, mutually compatible versions (Spring Boot 4 ⇒ Spring AI 2.x). Never guess Spring AI APIs — check the version in `pom.xml` and its official docs/sources before using a class or method.
4. Keep frontend and backend independent; they talk only via the REST API in `docs/ARCHITECTURE.md`.
5. Keep changes small; add tests for backend logic.
6. After each task, use the `change-explainer` skill and update `docs/PROGRESS.md`.
7. Get user approval before structural changes.

## Skills
- `chatbot-ui` — Angular chat UI work.
- `springboot-api` — REST endpoints, services, config.
- `rag-documents` — PDF ingestion, chunking, embeddings, retrieval, prompting.
- `change-explainer` — summarise every completed change.
