# OpenAnki

A sleek, modern Android flashcard app inspired by Anki. It imports standard `.apkg` decks and reads the Anki SQLite schema (`collection.anki2` / `collection.anki21`) so community decks work out of the box.

## Features
- Import `.apkg` decks with the system file picker
- Browse decks and card counts
- Study mode with reveal + grading controls
- Clean, modern UI with custom typography and atmospheric background

## Open in Android Studio
Open the `OpenAnki` folder as a Gradle project.

## Notes
- The scheduling buttons are UI-only in this starter build; extending to full SM-2 scheduling is straightforward in `MainViewModel`.
- Media files are extracted on import and can be wired up to render rich card content.
