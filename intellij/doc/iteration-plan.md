
# EcoScoop - Iteration Plan

## Plan for phase #2 (elaboration part 1)

Use cases in decreasing priority order:
1. __Access Article__ - Accessing the article is the most crucial step and the main component for this EcoScoop
2. __Search Article__ - Another crucial step for the EcoScoop environment which is search for articles based off preferences
3. __Save Article__ - One of the functions needed for article uses and necessary for later implementation on View Profile
4. __React Article__ - One of the functions needed for article uses and necessary for later implementation on View Profile
5. __Access Saved Folder__ - Another function needed for article uses and necessary for later implementation on View Profile
6. __View Profile__ - Uses stats from save article, react article, and access history, along with user-based features for later UI/UX implementation
7. __Configure Settings__ - Ties in all application functionality with ease of access user preference and setting functions.
8. __View Dashboard__ - Main page of access and requires many external APIs for data

In phase #2, we hope to create a prototype featuring the first two use cases.

---

# Phase #3 Iteration Plan
Phase #3 has two main goals: implement our remaining use cases (View Profile, EcoBoard, Configure Settings) and migrate the project from our IntelliJ command-line app to Android Studio. To avoid extra rework, we will build new features directly in Android rather than implementing them in the command line first and converting later.

Most of our existing classes (Article, Folder, FeedFetcher, ArticleParser, etc.) are plain Java and can be moved into Android with little to no changes. The biggest update will be replacing CmdLineUI with a real Android interface (buttons, screens, navigation).

### 1. Moving from IntelliJ to Android Studio

Before adding new features, we need to separate what transfers cleanly and what must be redesigned.

### 2. What carries over easily:
Our backend logic (Article, Author, Source, Tag, Folder, FolderManager, ArticleRetriever, ArticleDatabase, FeedFetcher, ArticleParser, etc.) can be copied directly into Android Studio. The Controller also carries over since it only interacts with the UI through the UI interface.

### 3. What must be rebuilt:
CmdLineUI cannot transfer to Android because it depends on Scanner and System.out. Instead, we will create an AndroidUI implementation that uses Activities/Fragments and buttons instead of terminal input/output. Since the Controller only communicates through the UI interface, it should work with AndroidUI without needing major changes.

### 4. Build system changes:
Android Studio uses Gradle, not Maven. Our XML parser dependency (xpp3) will need to be moved from the pom.xml file into build.gradle.

### 5. Networking requirements:
Android does not allow network calls on the main thread. Any feed-fetching logic (ex: FeedFetcher.fetchAll()) must run in a background thread (ExecutorService or similar), then update the UI afterward.

### 6. Internet permissions:
We must add internet permissions in AndroidManifest.xml, otherwise feeds will fail to load.

## Use Cases for Phase #3
### 1. View Profile

The Profile feature gives users a place to see their progress and activity. It will display points, most-used tags, and article engagement stats (read/liked counts). This use case comes first because EcoBoard and Settings depend on having User/Profile data available. On Android, Profile will be its own Fragment accessible from the bottom navigation bar.

Needs: User class (username, points, activity history)
Profile class (calculates stats from user data)
Profile screen in the UI (Fragment on Android)
Controller methods to retrieve and send profile data to the UI
### 2. EcoBoard (Leaderboard + Points)

EcoBoard is the main “game-like” feature of EcoScoop. Users earn points for reading, liking, and saving articles, which determines their level and leaderboard rank. This comes second because it depends on the User/Profile structure from the Profile use case. On Android, EcoBoard will be a separate tab with a scrollable leaderboard list.

Needs: Point system tied to user actions (read, like, save)
Leaderboard class to rank users by points
Level thresholds based on point totals
EcoBoard screen in the UI
### 3. Configure Settings

Settings allows users to customize their display name, bio, and news preferences (local vs global). On the command line, this is a menu; on Android, it becomes a dedicated settings screen. This is listed third since it depends on the User/Profile system being established. Settings must persist between sessions, meaning Android will use SharedPreferences for small settings and Room (or another database approach) for larger stored data.

Needs: Settings stored in User or a separate Settings class
Controller methods for loadSettings() and saveSettings()
Settings screen in the UI
Persistent storage so preferences remain after closing the app