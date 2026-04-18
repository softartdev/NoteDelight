# Security Policy

## Supported Versions

Only the latest released version of NoteDelight receives security updates.
Older releases will not be patched; please upgrade to the latest version
before reporting a vulnerability.

| Version | Supported          |
| ------- | ------------------ |
| 8.5.x   | :white_check_mark: |
| < 8.5   | :x:                |

## Reporting a Vulnerability

**Please do not report security vulnerabilities through public GitHub
issues, discussions, or pull requests.**

Instead, report them privately using one of the following channels:

- **GitHub Security Advisories** (preferred): open a private report at
  https://github.com/softartdev/NoteDelight/security/advisories/new
- **Email**: send details to artik222012@gmail.com. If you wish to encrypt
  your report, request a PGP key in your first message.

Please include as much of the following as you can:

- A description of the issue and the affected component (Android, iOS,
  desktop, or shared module).
- The version / commit of NoteDelight you tested against.
- Step-by-step instructions to reproduce the issue.
- Proof-of-concept code, logs, or screenshots, if available.
- The potential impact (data disclosure, code execution, bypass of the
  note password, etc.).

### What to expect

- **Acknowledgement** within 72 hours of your report.
- **Initial assessment** (accepted / needs more info / declined) within
  7 days.
- **Status updates** at least every 14 days while the issue is being
  worked on.
- **Fix and disclosure**: once a fix is ready, a new release will be
  published and the advisory made public. Reporters who wish to be
  credited will be acknowledged in the advisory and CHANGELOG.

### Scope

In scope:

- The NoteDelight Android, iOS, web, and desktop apps built from this
  repository.
- The shared Kotlin Multiplatform modules, including note storage and
  encryption.

Out of scope:

- Vulnerabilities in third-party dependencies that are already tracked
  upstream (please report those to the upstream project; you may still
  notify us so we can bump the dependency).
- Issues that require a physical, already-unlocked device with the app
  open.
- Social-engineering or phishing attacks against users or maintainers.
