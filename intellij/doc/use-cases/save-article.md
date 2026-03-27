# Save Article

## 1. Primary actor and goals

__User__: Ease of access storing articles for later reading or saving them in preferred folders. 

## 2. Other stakeholders and their goals

* __Author__: Wants to know how many saves their article has gained.


## 3. Preconditions
* User is authenticated
* User switches to Article Section
* User accesses Article
* User has clicked Save Article Button

## 4. Postconditions
* Stores Article into a Saved Folder
* Author is able to view how much saves

## 5. Workflow

```plantuml
@startuml

skin rose

title Save Article (Casual)

'define the lanes
|#application|User|
|#implementation|System|

|System|
start
if (Article is Saved?) then (no)
:Display Saved Article Folders;
|User|
if(Update Saved Folders?) then (yes)
:Select which Folder to save Article in;
else()
:Create New Folder;
|System|
:Save New Folder to Folders;
endif
|System|
:Send Article to save;
 else ()
:Remove Article;
stop
endif


|System|
if (Validate ID) then (yes)
:Save Article to user's preferred location;
:Update amount of saves on Article;
stop

else (no)
:Do not save article;
:Display Unable to Save Article;

stop
@enduml
```

## 6. Sequence Diagram
```plantuml
@startuml
skin rose
hide footbox
title Save Article to Folder

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR
participant "fm : FolderManager" as FM

UI -> user : display save prompt\n(0. No / 1. Yes)
user -> UI : enters 1
UI -> user : "Enter folder name: "
user -> UI : enters folderName
UI -> controller : onSaveToFolder(articleId, folderName)
controller -> AR : saveToFolder(articleId, folderName)
AR -> FM : saveToFolder(articleId, folderName)
FM -> FM : getFolder(folderName)

alt folder does not exist
  create participant "folder : Folder" as F
  FM -> F : folder = new Folder(name, retriever)
  FM -> FM : folders.add(folder)
else folder exists
  FM -> F : folder found
end

FM -> F : addArticle(articleId)
F -> AR : getArticle(articleId)
AR --> F : art : Article
F -> F : articleIds.add(id)

UI --> user : "Saved to folder 'folderName'."

@enduml

```

```plantuml
@startuml
skin rose
hide footbox
title Save Article Declined

actor User as user
participant "ui : CmdLineUI" as UI

UI -> user : display save prompt\n(0. No / 1. Yes)
user -> UI : enters 0
UI --> user : return to article list

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Remove Article from Folder

actor User as user
participant "ui : CmdLineUI" as UI
participant "folder : Folder" as F

user -> UI : remove article from folder
UI -> F : removeArticle(id)
F -> F : articleIds.remove(id)
UI --> user : show updated folder contents

@enduml
```