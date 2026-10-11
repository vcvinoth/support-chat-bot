---
name: change-explainer
description: Explain a completed change in plain language and update docs/PROGRESS.md. Use at the end of every task, or when the user asks what changed and why.
---

# Change Explainer

At the end of each task, reply with these sections, kept short:

1. **Summary** — one or two sentences on what was done.
2. **Files changed** — each path with a one-line purpose (created / modified / deleted).
3. **Why** — the reason for key decisions; mention alternatives only if relevant.
4. **How to verify** — exact commands to run and what to expect (tests, build, manual check).
5. **Dependencies/versions** — any added or changed, or "none".
6. **Not done / risks** — skipped steps, failing tests, known gaps. Be honest.
7. **Next task** — the next step from `docs/PROGRESS.md`.

Then update `docs/PROGRESS.md`: tick the finished item under **Done** (with date), and set **Next task**.

Rules: no marketing language, no restating code line by line, no claims you did not verify.
