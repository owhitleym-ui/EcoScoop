
# EcoScoop - Iteration Plan

## Plan for phase #2 (elaboration part 1)

Use cases in decreasing priority order:
1. __Access Article__ - Accessing the article is the most crucial step and the main component for this EcoScoop
2. __Search Article__ - Another crucial step for the EcoScoop environment which is search for articles based off preferences
3. __Save Article__ - One of the functions needed for article uses and necessary for later implementation on View Profile
4. __React Article__ - One of the functions needed for article uses and necessary for later implementation on View Profile
5. __Access Saved Folder__ - Another function needed for article uses and necessary for later implementation on View Profile
6. __View Profile__ - Uses stats from save article, react article, and access history, along with user-based features for later view.UI/UX implementation
7. __Configure Settings__ - Ties in all application functionality with ease of access user preference and setting functions.
8. __View Dashboard__ - Main page of access and requires many external APIs for data

In phase #2, we hope to create a prototype featuring the first two use cases.

---

# Phase #3 Iteration Plan

## Use Cases for Phase #3 (Sequence Diagrams Already Done)
__1. View Profile__ - The Profile feature gives users a place to see their progress and activity.
- It will display points, most-used tags, and article engagement stats (read/liked counts).
- This use case comes first because Settings depend on having User/Profile data available. On Android, Profile will be its own Fragment accessible from the bottom navigation bar.

__2. View Dashboard__ - Dashboard is necessary to display overall environmental statistics in readable charts.
- There will be the dashboard tab in the view.UI that the User begins in. It is necessary to figure out the visual elements in Android Studio because it is primarily graphics.

__3. Configure Settings__ - Settings allows users to change their name, bio, and news preferences (local vs global).
- On the command line, this is a menu; on Android, it becomes a settings screen located in Profile tab.
- This is third since it depends on the User/Profile system being established. Settings must save between sessions, meaning Android will use SharedPreferences for small settings and a database approach for larger stored data.
Our goals: 
- Implement our remaining use cases (View Profile, EcoBoard, Configure Settings) and transfer the project from IntelliJ's command-line to Android Studio. 
- We will build new features directly in Android so we don't have to implement them both in the command line first and converting later to Android. Our existing classes are Java and can be moved into Android with little to no changes.
- The biggest update will be replacing view.CmdLineUI with a real Android interface (buttons, screens, navigation).
- 
## Android Studio
- Our backend logic of all of our classes except the view.CmdLineUI can be copied directly into Android Studio when we input the package for Empty Views Activity. The controller.Controller also carries over since it only interacts with the view.UI through the view.UI interface.
- We will create an AndroidUI implementation that uses Fragments and buttons instead of terminal input/output. 
- Since the controller.Controller only communicates through the view.UI interface, it should work with AndroidUI without needing major changes. We will have to figure out all the graphics, text-wrapping, etc.
- Since Android Studio uses Gradle, our XML parser dependency (xpp3) will need to be moved from the pom.xml file into build.gradle.
- Android does not allow network calls on the main thread. Any feed-fetching logic must run in a background thread (ExecutorService or similar), then update the view.UI afterward. 
- We must add internet permissions in AndroidManifest.xml, otherwise feeds will fail to load.

---

# Phase #4 Iteration Plan (Final)

## Prioritization Criteria

**Risk** — how likely the feature is to break or be hard to implement (high risk = lots of unknowns or no existing code).

**Coverage** — how much of the backend is already built and tested. High coverage means the model and logic are done, we just need the UI.

**Criticality** — how important the feature is to the core experience of EcoScoop as an environmental news app.

---

## Use Cases for Phase #4

__1. React Article__ — *Criticality: High | Risk: Low | Coverage: High*
- The backend is already done: `addLike()`, `addDislike()`, and `addComment()` are implemented and tested on `Article`.
- The only missing piece is the UI — like/dislike buttons and a comment input need to be wired into `DisplayArticleFragment` and connected to the controller.
- This is high priority because it is a core engagement feature and the easiest to finish since the model is complete.

__2. View Profile__ — *Criticality: Medium | Risk: High | Coverage: Low*
- No `Profile` class exists yet. Will need to be built from scratch.
- Should display basic stats (articles liked, comments left, saved folders count) pulled from existing article and folder data.
- Lower priority than the above three because it depends on React and Save Article being finished first, and it requires the most new code.

__3. Configure Settings__ — *Criticality: Low | Risk: High | Coverage: Low*
- No settings persistence exists yet. Would require `SharedPreferences` for storing user preferences between sessions.
- Lowest priority for this iteration since it depends on Profile being set up and adds a lot of new complexity for a feature that is not critical to the core news-reading experience.

## Goals for Phase #4
- Need to integrate article thumbnails/images for search feed and inside the article itself.
- Complete the Save Article flow (folder picker dialog, controller wiring).
- Build the Access Saved Folders screen so users can browse and reopen saved articles.
- Add in folder deletion and editing abilities.
- Add a basic View Profile screen that shows user activity stats.
- Configure Settings is a stretch goal if time allows.
- Set up structure and simple visuals for Eco Dashboard.
- Add more visual interest: drawings for each tab and a logo for our app.

## What Was Completed in Phase #4

- **Article images** — the parser now extracts image URLs from `<media:content>` tags and `content:encoded` HTML. Images are loaded into card headers on the feed using the Glide library, which handles background threading and caching automatically.
  - **Limitation:** images only appear on the article feed cards. The article detail screen does not yet show the header image. Some RSS feeds do not provide image URLs, so those cards show a blank placeholder.
- **Like/Dislike toggling** — reactions now toggle on and off and are mutually exclusive (liking removes a dislike and vice versa), tracked via a `userReaction` field on each `Article`.
- **Comments** — users can post comments on articles; they appear as a bulleted list below the article body.
- **Save to Folder** — tapping Save opens a dialog for a folder name. The article is saved into that folder via `FolderManager`.
- **Profile screen** — shows all saved articles across all folders. Displays an empty state message when nothing has been saved.
- **Espresso UI tests** — added instrumented tests for navigation, article interactions (reactions, comments, saving), and search.
- **Naming cleanup** — `ArticleDatabase` was extracted as an interface; the RSS fetcher was renamed `ArticleRepository` and the search/sort layer renamed `ArticleRetriever` to better reflect their roles.

---

## Future Work: Data Persistence

Currently all user data — liked articles, comments, and saved folders — is stored in memory only and is lost when the app is closed. To fix this, the app needs a persistence layer.

The most practical option for this project would be **Firebase Firestore**, a cloud database from Google that integrates easily with Android. In an MVC structure, the model layer (`ArticleRepository`, `FolderManager`, `Article`) would be responsible for reading and writing to Firestore — the controller and view would not need to change. Each user's saved folders, reactions, and comments would be stored as documents and synced automatically across sessions.


