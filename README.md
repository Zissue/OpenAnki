# OpenAnki

A sleek, modern Android flashcard app inspired by Anki. It imports standard `.apkg` decks and reads the Anki SQLite schema (`collection.anki2` / `collection.anki21`) so community decks work out of the box.

## Features
- Import `.apkg` decks with the system file picker
- Browse decks and card counts
- Study mode with reveal + grading controls
- Session-based spaced repetition: cards graded Again or Hard are re-queued within the session
- Card shuffling for varied study order each session
- HTML stripping for cleaner card content display
- Deck deletion from the library
- Deck search and filtering by name
- Session completion summary with grade breakdown (Again / Hard / Good / Easy)
- Clean, modern UI with custom typography and atmospheric background

## Open in Android Studio
Open the `OpenAnki` folder as a Gradle project.

## Notes
- Session-based spaced repetition re-queues Again/Hard cards within the current study session; full SM-2 cross-session scheduling can be added in `MainViewModel`.
- Media files are extracted on import and can be wired up to render rich card content.
