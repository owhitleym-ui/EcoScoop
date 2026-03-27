# EcoSCOOP

An engaging and dynamic News Hub Application that aggregates environmental news from RSS feeds to deliver sustainable and environmentally-focused news with real-time updates. Users can browse, search, react to, and organise articles into folders.

## Features

- **Live RSS fetching** from Earth911, Grist, and Carbon Brief
- **Browse & read** articles with word-wrapped full-text display
- **React (Like, Dislike, Comment)** articles are shown with reaction stats from user
- **Search** by keyword, tag, or author with relevance-ranked results
- **model.Folder Management** to store folders for next uses

## Limitations

- All data is only stored per session and cannot be remembered
- The two RSS sources are hardcoded and cannot be changed.
- Text-base leaves less interactable uses
- Cannot access folders due to no user profile implementation
- Search uses simple substring matching

## How to Run

### Prerequisites

- Java 21+
- `xmlpull` and an implementation like `kxml2` on the classpath
- Internet connection (feeds are fetched live on startup)

### Compile & Run
The entry point is `controller.Controller.main()`. A secondary `Main.main()` is available for quick testing — it prints all article summaries and exits. You can find these in the files './src/main/java/controller.Controller'

## Project Structure

| File | Role |
|---|---|
| `controller.Controller.java` | Entry point; mediates between view.UI and data layer |
| `view.CmdLineUI.java` | Command-line view.UI implementation |
| `view.UI.java` | Interface defining screens and listener callbacks |
| `model.ArticleRetriever.java` | Data access, search, sort, and folder delegation |
| `model.ArticleDatabase.java` | Initialises feeds, triggers fetch/parse, stores articles |
| `model.FeedFetcher.java` | Downloads RSS feeds over HTTP |
| `model.ArticleParser.java` | Parses RSS/XML into `model.Article` objects |
| `model.FolderManager.java` | Manages the collection of user-created folders |
| `model.Article.java` | model.Article model (content, metadata, likes/dislikes, search helper) |
| `model.Folder.java` | Named collection of saved article IDs |
| `model.Author.java` | model.Author model |
| `model.Tag.java` | model.Tag model |
| `model.Source.java` | model.Source model (site name, URL, publish date) |
| `Main.java` | Standalone test harness |
