# Save Article

## 1. Primary actor and goals

__User__: Wants to save articles into named folders for later reference. Folders are created on demand and listed on the Profile screen.

## 2. Other stakeholders and their goals

* __Authors__: Benefit from knowing how many users saved their article.

## 3. Preconditions
* User has opened an article in the Display Article screen.

## 4. Postconditions
* Article is stored in the named folder (folder is created if it does not exist).
* Folder appears on the Profile screen.

## 5. Workflow
```plantuml
@startuml
skin rose

title Save Article (Casual)

|#application|User|
|#implementation|System|

|User|
start
:Tap Save button on article;

|System|
:Show folder picker dialog\n(existing folders + "New folder" option);

|User|
if (Choose existing folder?) then (yes)
  :Select folder name;
else (create new)
  :Enter new folder name;
endif

|System|
:saveToFolder(articleId, folderName);
if (Folder exists?) then (no)
  :Create new Folder;
  :Add to FolderManager;
endif
:Add article ID to folder;

|User|
:Confirmation shown;
stop
@enduml
```

## 6. Sequence Diagrams

```plantuml
@startuml
skin rose
hide footbox
title Save Article to Folder

actor User as user
participant "fragment : DisplayArticleFragment" as UI
participant "activity : ControllerActivity" as controller
participant "fm : FolderManager" as FM
participant "folder : Folder" as F
participant "ar : ArticleRetriever" as AR

user -> UI : taps Save, enters folder name
UI -> controller : onSaveToFolder(articleId, folderName)
controller -> FM : saveToFolder(articleId, folderName)
FM -> FM : getFolder(folderName)

alt folder does not exist
  create F
  FM -> F : new Folder(name, retriever)
  FM -> FM : folders.add(folder)
else folder exists
  FM -> F : (existing folder)
end

FM -> F : addArticle(articleId)
F -> AR : getArticle(articleId)
AR --> F : Article (validates ID exists)
F -> F : articleIds.add(id)

UI --> user : "Saved to 'folderName'"

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Remove Article from Folder

actor User as user
participant "fragment : ProfileFragment" as UI
participant "folder : Folder" as F

user -> UI : opens folder, taps remove on article
UI -> F : removeArticle(id)
F -> F : articleIds.remove(id)
UI --> user : updated folder contents

@enduml
```
