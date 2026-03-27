
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
### 1. View Profile
The Profile feature gives users a place to see their progress and activity.
- It will display points, most-used tags, and article engagement stats (read/liked counts).
- This use case comes first because Settings depend on having User/Profile data available. On Android, Profile will be its own Fragment accessible from the bottom navigation bar.

### 2. View Dashboard

EcoDashboard is necessary to display overall environmental statistics in readable charts.
- There will be the dashboard tab in the view.UI that the User begins in. It is necessary to figure out the visual elements in Android Studio because it is primarily graphics.

### 3. Configure Settings

Settings allows users to change their name, bio, and news preferences (local vs global).
- On the command line, this is a menu; on Android, it becomes a settings screen located in Profile tab.
- This is third since it depends on the User/Profile system being established. Settings must save between sessions, meaning Android will use SharedPreferences for small settings and a database approach for larger stored data.
Our goals: 
- Implement our remaining use cases (View Profile, EcoBoard, Configure Settings) and transfer the project from IntelliJ's command-line to Android Studio. 
- We will build new features directly in Android so we don't have to implement them both in the command line first and converting later to Android. Our existing classes are Java and can be moved into Android with little to no changes.
- The biggest update will be replacing view.CmdLineUI with a real Android interface (buttons, screens, navigation).

### 1. What carries over:
- Our backend logic of all of our classes except the view.CmdLineUI can be copied directly into Android Studio when we input the package for Empty Views Activity. The controller.Controller also carries over since it only interacts with the view.UI through the view.UI interface.

### 2. What must be rebuilt:
- We will create an AndroidUI implementation that uses Fragments and buttons instead of terminal input/output. 
- Since the controller.Controller only communicates through the view.UI interface, it should work with AndroidUI without needing major changes. We will have to figure out all the graphics, text-wrapping, etc.

### 4. Build system changes:
- Since Android Studio uses Gradle, our XML parser dependency (xpp3) will need to be moved from the pom.xml file into build.gradle.

### 5. Feed and updating requirements:
- Android does not allow network calls on the main thread. Any feed-fetching logic must run in a background thread (ExecutorService or similar), then update the view.UI afterward. 
- We must add internet permissions in AndroidManifest.xml, otherwise feeds will fail to load.

