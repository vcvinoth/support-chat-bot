---
name: rag-documents
description: Workflow for PDF ingestion and retrieval-augmented answers — PDFBox text extraction, chunking, local ONNX embeddings, Spring AI SimpleVectorStore, similarity search, and the grounded Claude prompt. Use for any ingestion, embedding, retrieval, or prompt work.
---

# RAG Documents

## Before coding
- Confirm the Spring AI version in `backend/pom.xml` and look up the real API for: Anthropic chat model/`ChatClient`, Transformers embedding model, `SimpleVectorStore`, text splitter, `SearchRequest`. Do not invent classes, builders, or property names.
- Claude has no embeddings API; embeddings come from the local Transformers (ONNX) model.

## Ingestion
1. Validate the file is a non-empty PDF.
2. Extract text page by page with PDFBox (`Loader.loadPDF`, `PDFTextStripper` with start/end page). Close the document.
3. Skip blank pages; fail clearly if the whole PDF has no text (likely scanned).
4. Split into chunks with Spring AI's token splitter; make chunk size configurable.
5. Metadata on every chunk: `documentId`, `fileName`, `page`.
6. Add chunks to the vector store; track document status (indexed/failed) in memory.

## Retrieval and answer
1. Similarity search with configurable `topK` and similarity threshold.
2. No results → return a fixed "I couldn't find this in the uploaded documents" answer; do not call Claude.
3. Prompt: system message says answer only from the provided context, say when unsure, be concise. Put chunks in a clearly delimited context block with file name + page.
4. Return the answer and de-duplicated sources from chunk metadata.

## Tests
- Generate test PDFs in memory with PDFBox (`TestPdfs`); never commit real or third-party documents.
- Test extraction, chunk metadata, and empty-result behaviour; mock Claude.
