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
@startuml@startuml
skin rose
hide footbox
title Save Article (Sequence)

actor User
participant ": System UI" as UI
participant ": Controller" as Controller
participant "f : Folder" as Folder

UI -> User : display save/unsave option

alt article not yet saved
    User -> UI : click save

    alt save to existing folder
        UI -> User : display folder list
        User -> UI : select folder
        UI -> Controller : saveToFolder(articleId, folderName)

    else create new folder
        User -> UI : create new folder
        UI -> Controller : createFolder(folderName)
        Controller -> Folder ** : f = create(folderName)
        UI -> Controller : saveToFolder(articleId, folderName)
    end

    Controller -> Controller : validateUserId()

    alt valid ID
        Controller --> UI : article saved\nupdate save count
    else invalid ID
        Controller --> UI : display unable to save
    end

else article already saved
    User -> UI : click unsave
    UI -> Controller : removeArticle(articleId)
    Controller --> UI : article removed
end

@enduml

```