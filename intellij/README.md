# EcoSCOOP

An engaging and dynamic News Hub Application that aggregates environmental news from RSS feeds to deliver sustainable and environmentally-focused news with real-time updates. Users can browse, search, react to, and organise articles into folders.

## Features

- **Live RSS fetching** from Grist, Carbon Brief, and Earth911
- **Browse & read** articles with word-wrapped full-text display
- **React (Like, Dislike, Comment)** on articles — reaction stats are shown when re-opening an article
- **Search** by keyword, tag, or author with relevance-ranked results
- **Folder Management** to save articles into named folders

## Limitations

- All data is only stored per session and is not persisted between runs
- RSS sources are hardcoded and cannot be changed at runtime
- Text-based interface only
- Cannot view saved folder contents (no folder browsing UI yet)
- Search uses simple substring matching

## How to Run

### Prerequisites

- Java 21+
- `xmlpull` and an implementation like `kxml2` on the classpath
- Internet connection (feeds are fetched live on startup)

### Compile & Run

The entry point is `controller.Controller`. Run via Maven:

```
mvn exec:java -Dexec.mainClass="controller.Controller"
```

## Project Structure

| File | Role |
|---|---|
| `controller/Controller.java` | Entry point; mediates between UI and data layer |
| `view/CmdLineUI.java` | Command-line UI implementation |
| `view/UI.java` | Interface defining screens and listener callbacks |
| `model/ArticleRetriever.java` | Data access, search, sort, and folder delegation |
| `model/ArticleDatabase.java` | Initialises feeds, triggers fetch/parse, stores articles |
| `model/FeedFetcher.java` | Downloads RSS feeds over HTTP |
| `model/ArticleParser.java` | Parses RSS/XML into Article objects |
| `model/FolderManager.java` | Manages the collection of user-created folders |
| `model/Article.java` | Article model (content, metadata, likes/dislikes/comments) |
| `model/Folder.java` | Named collection of saved article IDs |
| `model/Author.java` | Author model |
| `model/Tag.java` | Tag model |
| `model/Source.java` | Source model (site name, URL, publish date) |

