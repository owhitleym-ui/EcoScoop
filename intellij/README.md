# EcoSCOOP

An Android news and climate app that aggregates environmental news from RSS feeds and pairs it with live local weather and historical climate data — so users can read about climate change while seeing its effects in their own backyard.

## Testing Note
- **When running Espresso tests on Android** tester must interact with the location popup, While Using, or else tests will fail.
## Features

- **Live RSS feed** from Grist, Carbon Brief, and Earth911 — fetched on app launch
- **Article cards** with header images, titles, source, date, and tags
- **Full article view** with like/dislike (mutually exclusive toggles), inline comments, and save-to-folder
- **Search** by keyword, tag, or author — results ranked by relevance; sort by Oldest First or Source A–Z
- **Save to folder** — articles saved into named folders; folders auto-created on first save
- **Profile screen** — shows activity stats (articles read, liked, disliked), settings toggles, saved folders, and comment history with per-comment removal
- **Settings** — metric/imperial toggle (affects all dashboard units); local vs. global location mode
- **Eco Dashboard** (default landing screen):
  - Current temperature, wind, precipitation, humidity, UV Index, Feels Like, and Water Stress (ET₀) cards — each tappable for a detail bottom sheet
  - 7-day forecast strip
  - Climate anomaly card comparing today's temperature to a 3-month ERA5 historical baseline
  - GPS location detection with city name reverse-geocoding; 15-second timeout falls back to New York
  - City search via Open-Meteo Geocoding API
  - GPS refresh button to re-acquire device location

## Limitations
- RSS sources are hardcoded; the feed list cannot be changed at runtime
- Article content is fetched once at launch and is not paginated — the feed shows only what was available at startup and cannot scroll to load more articles
- Profile does not yet support customization — there is no profile picture, display name, or bio
- Location requires the device's `ACCESS_FINE_LOCATION` permission; without it the dashboard defaults to New York coordinates
- Climate baseline data uses ERA5 reanalysis, which has a ~1-year lag — the baseline covers the prior calendar year's Q4
- Evapotranspiration (ET₀) is always displayed in mm/day regardless of the metric/imperial toggle (it is a soil-science unit with no standard imperial equivalent)
- Article images depend on the RSS feed including an image URL in `<media:content>` or `content:encoded`; feeds without images show no image

## APIs & Libraries

### External APIs / Services

| API | Purpose |
|---|---|
| **Open-Meteo Forecast API** | Fetches current conditions and 7-day forecast (temperature, precipitation, wind, UV index, feels-like, ET₀) for a given lat/lon; accessed via the official Open-Meteo Android SDK |
| **Open-Meteo ERA5 Archive API** | Retrieves historical daily climate averages used to compute the temperature anomaly card on the dashboard; accessed via the Open-Meteo SDK |
| **Open-Meteo Geocoding API** | Converts a city name typed into the dashboard search bar into lat/lon coordinates for weather lookup; accessed via the Open-Meteo SDK |
| **Firebase Firestore** | Cloud persistence for user accounts, saved article folders, and article reactions (likes, dislikes, comments) |

### Libraries

| Library | Purpose |
|---|---|
| **Open-Meteo SDK** (`com.open-meteo:sdk`) | Official Android SDK that wraps all Open-Meteo HTTP calls and handles FlatBuffers deserialization of the API responses |
| **Glide** (`com.github.bumptech.glide`) | Loads and caches article header images from RSS-provided URLs into the article feed and detail views |
| **XPP3** (`xpp3:xpp3`) | XML pull parser used by `ArticleParser` to parse raw RSS/XML feeds from Grist, Carbon Brief, and Earth911 into `Article` objects |
| **Google Play Services Location** | Provides `FusedLocationProviderClient` for GPS-based device location with a 15-second timeout fallback |

## How to Run (Android)

### Prerequisites

- Android Studio (latest stable)
- An Android emulator or physical device running API 33+
- Internet connection (RSS feeds and weather data are fetched live on startup)

### Steps

1. Open the `EcoScoop` folder in Android Studio
2. Let Gradle sync finish
3. Start an emulator from Device Manager (grant location permission when prompted)
4. Click Run (▶) on the `app` configuration

### Running Tests

- **Unit tests** (no device needed): right-click the `test` folder → Run Tests
- **Espresso UI tests** (requires emulator): right-click `NavigationTest` → Run individually

## Project Structure (Android)

| File | Role |
|---|---|
| `controller/ControllerActivity.java` | Main activity; implements all UI Listener interfaces; manages GPS, geocoding, weather fetch, and fragment navigation |
| `controller/ArticleRetriever.java` | Article lookup, keyword/tag/author search, and sort (date, oldest, source, relevance) |
| `controller/EcoDataFetcher.java` | Fetches weather and ERA5 climate data from Open-Meteo JSON API on a background thread |
| `controller/EcoDataRetriever.java` | Null-safe accessor facade over EcoDatabase |
| `controller/FeedFetcher.java` | Downloads raw RSS XML over HTTP |
| `view/ArticleFeedFragment.java` | Scrollable article card list with Glide image loading |
| `view/DisplayArticleFragment.java` | Full article view with like/dislike, comments, and save dialog |
| `view/SearchArticleFragment.java` | Search screen with keyword/tag/author chips and sort chips (Relevance / Oldest First / Source A–Z) |
| `view/DashboardFragment.java` | Weather + climate dashboard with tappable metric cards and 7-day forecast strip |
| `view/ProfileFragment.java` | User stats, settings, saved folders, and removable comment history |
| `model/ArticleRepository.java` | Implements `ArticleDatabase`; fetches and parses all RSS feeds on construction |
| `model/ArticleParser.java` | Parses RSS/XML into Article objects using XmlPullParser; extracts image URLs |
| `model/FolderManager.java` | Create, delete, rename, and query named article folders |
| `model/Folder.java` | Named collection of article UUIDs; validates IDs against the retriever on add |
| `model/User.java` | Session user: username, metric/location prefs, comment list, activity counters |
| `model/WeatherData.java` | Immutable value object for all Open-Meteo forecast fields |
| `model/ClimateData.java` | Immutable value object for ERA5 historical climate arrays |
| `model/EcoRepository.java` | Implements `EcoDatabase`; holds latest WeatherData and ClimateData in memory |
| `model/Article.java` | Article entity with UUID id, metadata, reactions, and comments |
| `model/Author.java` | Author value object |
| `model/Tag.java` | Tag value object |
| `model/Source.java` | Source value object (site name, URL, publish date) |
| `persistence/FirestoreFacade.java` | Cloud Firestore persistence for user data and folders |
| `persistence/LocalStorageFacade.java` | SharedPreferences fallback persistence |

## IntelliJ / Command-Line Version

The `intellij/` directory contains the original command-line prototype built before the Android migration. It shares the same article model logic but uses a text-based `CmdLineUI`. It is no longer the active version of the app; the Android project is the canonical implementation.
