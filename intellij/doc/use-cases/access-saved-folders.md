# Access Saved Folders

## 1. Primary actor and goals

__User__: Wants to look through previous articles that they have read or saved. Ease of access rereading articles, accessing history, and reviewing saved content.

## 2. Other stakeholders and their goals
* No other stakeholders.

## 3. Preconditions
* User is authenticated
* User switches to view profile tab.
* User clicks settings tab.
* User clicks saved folders tab.

## 4. Postconditions
* User has reviewed and/or accessed saved articles or history.
* When an article is accessed again, just as in the use case history is updated.

## 5. Workflow
```plantuml
@startuml

skin rose

title Access Saved Folders (Casual)
'define the lanes
|#application|User|
|#implementation|System|

|User|
start

repeat
switch(Choose folder?)

case(Open history folder?)
:Click History folder;

case(Open other folder?)
:Click other folder;
endswitch

|System|

:Display Folders of Read Articles;

|User|
:Select folder;
|System|
if(History?) 
:Fetch history and display from
newest to oldest accessed;
elseif(Other?)
:Display articles in folder;
endif
|User|
repeat while (Continue browsing Folders?) is (yes) not (no)
|System|
switch (Reread article?) 

case (Returned to Profile?)
    :Reset to Profile tab;
case(Opened article?)
    :Execute __Access Article__;
endswitch

|User|


stop
@enduml
```

## 6. Sequence Diagram
```plantuml
@startuml
skin rose
hide footbox

title View All Folders

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR
participant "fm : FolderManager" as FM

user -> UI : view folders
UI -> controller : onGetFolders()
controller -> AR : getFolders()
AR -> FM : getFolders()
FM --> AR : List<Folder>
AR --> controller : List<Folder>
controller -> UI : displayFolders(List<Folder>)
UI --> user : show folder list

@enduml
```

```plantuml
@startuml
skin rose
hide footbox

title Open a Folder

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR
participant "fm : FolderManager" as FM
participant "folder : Folder" as F

user -> UI : select folder by name
UI -> controller : onGetFolder(name)
controller -> AR : getFolder(name)
AR -> FM : getFolder(name)
FM --> AR : folder : Folder
AR --> controller : folder
controller -> UI : displayFolder(folder)
UI -> F : open()

loop for each articleId in folder
  F -> AR : getArticle(id)
  AR --> F : art : Article
end

F --> UI : List<Article>
UI --> user : show folder contents

@enduml
```

```plantuml
@startuml
skin rose
hide footbox

title Rename Folder

actor User as user
participant "ui : CmdLineUI" as UI
participant "folder : Folder" as F

user -> UI : rename folder
UI -> F : rename(newName)
UI --> user : show updated name

@enduml
```

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

user -> UI : enters folder name
UI -> controller : onSaveToFolder(articleId, folderName)
controller -> AR : saveToFolder(articleId, folderName)
AR -> FM : saveToFolder(articleId, folderName)
FM -> FM : getFolder(folderName)

alt folder does not exist
  create participant "folder : Folder" as F
  FM -> F : folder = new Folder(name, retriever)
end

FM -> F : addArticle(articleId)

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

user -> UI : remove article
UI -> F : removeArticle(id)
UI --> user : show updated contents

@enduml
```

```plantuml
@startuml
skin rose
hide footbox

title Delete Folder

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR
participant "fm : FolderManager" as FM

user -> UI : delete folder
UI -> controller : onDeleteFolder(name)
controller -> AR : deleteFolder(name)
AR -> FM : deleteFolder(name)
FM --> AR : boolean
AR --> controller : boolean
controller -> UI : confirmDeletion()
UI --> user : return to folder list

@enduml
```

```plantuml
@startuml
skin rose
hide footbox

title Open Article from Folder

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR

user -> UI : select article from folder
ref over user, UI, controller, AR
  Access Article(id)
end ref

@enduml
```