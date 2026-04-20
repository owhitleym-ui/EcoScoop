# EcoSCOOP

An engaging and dynamic News Hub Application that aggregates environmental news from RSS feeds to deliver sustainable and environmentally-focused news with real-time updates. Users can browse, search, react to, and organise articles into folders.

## Features

- **Live RSS fetching** from Grist, Carbon Brief, and Earth911
- **Article feed** with header images loaded from each RSS source
- **Browse & read** articles with full-text display
- **React (Like, Dislike, Comment)** on articles — toggles between liked/disliked, mutual exclusion enforced
- **Search** by keyword, tag, or author with relevance-ranked results and date sorting
- **Save to folder** — save articles into named folders via a popup dialog
- **Profile screen** — shows all saved articles across folders
- **Dashboard** — main landing screen on app launch

## Limitations

- All data is stored in memory only — reactions, comments, and saved folders are lost when the app is closed
- RSS sources are hardcoded and cannot be changed at runtime
- Article header images only appear on the feed cards, not on the article detail screen
- Images depend on each RSS feed providing an image URL in `<media:content>` or `content:encoded` HTML — feeds that don't include images will show a blank placeholder
- Search uses simple substring matching
- Like/dislike counts and comments are per-session only and reset on relaunch

## How to Run (Android)

### Prerequisites

- Android Studio (latest stable)
- An Android emulator or physical device running API 33+
- Internet connection (RSS feeds are fetched live on startup)

### Steps

1. Open the `EcoScoop` folder in Android Studio
2. Let Gradle sync finish
3. Start an emulator from Device Manager
4. Click Run (▶) on the `app` configuration

### Running Tests

- **Unit tests** (no device needed): right-click the `test` folder → Run
- **Espresso UI tests** (requires emulator): right-click `SearchTest`, `ArticleInteractionTest`, or `NavigationTest` individually → Run

## Project Structure (Android)

| File | Role |
|---|---|
| `controller/ControllerActivity.java` | Main activity; implements all UI listener interfaces and mediates between view and model |
| `view/ArticleFeedFragment.java` | Scrollable article card list with Glide image loading |
| `view/DisplayArticleFragment.java` | Full article view with like/dislike, comments, and save dialog |
| `view/SearchArticleFragment.java` | Search screen with keyword/tag/author chips and sort toggle |
| `view/ProfileFragment.java` | Saved articles list, shows empty state when nothing is saved |
| `model/ArticleRepository.java` | Fetches all RSS feeds on construction, implements `ArticleDatabase` |
| `model/ArticleRetriever.java` | Handles article lookup, search, and sort against an `ArticleDatabase` |
| `model/ArticleDatabase.java` | Interface for the article data source |
| `model/FeedFetcher.java` | Downloads RSS feeds over HTTP |
| `model/ArticleParser.java` | Parses RSS/XML into Article objects, extracts image URLs |
| `model/FolderManager.java` | Manages user-created folders |
| `model/Article.java` | Article model — content, metadata, reactions, comments, image URL |
| `model/Folder.java` | Named collection of saved article IDs |
| `model/Author.java` | Author model |
| `model/Tag.java` | Tag model |
| `model/Source.java` | Source model (site name, URL, publish date) |

## IntelliJ / Command-Line Version

The `intellij/` directory contains the original command-line prototype built before the Android migration. It shares the same model logic but uses a text-based UI (`CmdLineUI`). It is no longer the active version of the app.
