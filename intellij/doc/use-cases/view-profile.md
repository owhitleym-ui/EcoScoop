# View Profile

## 1. Primary actor and goals
__User__: Wants to review their activity stats, browse saved folders, and review or remove their comment history.

## 2. Other stakeholders and their goals
None.

## 3. Preconditions
* User taps the Profile tab.

## 4. Postconditions
* User stats (articles read, liked, disliked) are displayed.
* Saved folders list is shown with article counts.
* Comment history is shown with a remove button per entry.

## 5. Workflow
```plantuml
@startuml
skin rose

title View Profile (Casual)

|#application|User|
|#implementation|System|

|User|
start
:Tap Profile Tab;

|System|
:Load user stats\n(articles read, liked, disliked);
:Load saved folders from FolderManager;
:Load comment history from User;
:Render profile screen;

|User|
:Browse stats and folder list;

if (Open a folder?) then (yes)
  |System|
  :Folder.open() → List<Article>;
  :Show article list in folder;
  |User|
  :Browse folder contents;
endif

if (Remove a comment?) then (yes)
  |User|
  :Tap ✕ on comment;
  |System|
  :User.removeComment(index);
  :Refresh comment list;
endif

|User|
:Navigate away;
stop
@enduml
```

## 6. Sequence Diagrams

```plantuml
@startuml
skin rose
hide footbox
title Load Profile Screen

actor User as user
participant "fragment : ProfileFragment" as UI
participant "activity : ControllerActivity" as controller
participant "u : User" as userModel
participant "fm : FolderManager" as FM

user -> UI : taps Profile tab
UI -> controller : onGetUserComments()
controller -> userModel : getComments()
userModel --> controller : List<String>
controller --> UI : comments

UI -> controller : onGetFolders()
controller -> FM : getFolders()
FM --> controller : List<Folder>
controller --> UI : folders

UI --> user : stats, settings toggles,\nfolders list, comments with ✕ buttons

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Remove Comment from Profile

actor User as user
participant "fragment : ProfileFragment" as UI
participant "activity : ControllerActivity" as controller
participant "u : User" as userModel

user -> UI : taps ✕ next to a comment
UI -> UI : AlertDialog — confirm Remove
user -> UI : confirms
UI -> controller : onRemoveComment(index)
controller -> userModel : removeComment(index)
UI -> UI : loadComments()
UI --> user : updated comment list

@enduml
```
