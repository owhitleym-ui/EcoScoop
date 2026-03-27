# Save model.Article

## 1. Primary actor and goals

__User__: Ease of access storing articles for later reading or saving them in preferred folders. 

## 2. Other stakeholders and their goals

* __Author__: Wants to know how many saves their article has gained.


## 3. Preconditions
* User is authenticated
* User switches to model.Article Section
* User accesses model.Article
* User has clicked Save model.Article Button

## 4. Postconditions
* Stores model.Article into a Saved model.Folder
* model.Author is able to view how much saves

## 5. Workflow

```plantuml
@startuml

skin rose

title Save model.Article (Casual)

'define the lanes
|#application|User|
|#implementation|System|

|System|
start
if (model.Article is Saved?) then (no)
:Display Saved model.Article Folders;
|User|
if(Update Saved Folders?) then (yes)
:Select which model.Folder to save model.Article in;
else()
:Create New model.Folder;
|System|
:Save New model.Folder to Folders;
endif
|System|
:Send model.Article to save;
 else ()
:Remove model.Article;
stop
endif


|System|
if (Validate ID) then (yes)
:Save model.Article to user's preferred location;
:Update amount of saves on model.Article;
stop

else (no)
:Do not save article;
:Display Unable to Save model.Article;

stop
@enduml
```

## 6. Sequence Diagram
```plantuml
@startuml@startuml
skin rose
hide footbox
title Save model.Article (Sequence)

actor User
participant ": System view.UI" as view.UI
participant ": controller.Controller" as controller.Controller
participant "f : model.Folder" as model.Folder

view.UI -> User : display save/unsave option

alt article not yet saved
    User -> view.UI : click save

    alt save to existing folder
        view.UI -> User : display folder list
        User -> view.UI : select folder
        view.UI -> controller.Controller : saveToFolder(articleId, folderName)

    else create new folder
        User -> view.UI : create new folder
        view.UI -> controller.Controller : createFolder(folderName)
        controller.Controller -> model.Folder ** : f = create(folderName)
        view.UI -> controller.Controller : saveToFolder(articleId, folderName)
    end

    controller.Controller -> controller.Controller : validateUserId()

    alt valid ID
        controller.Controller --> view.UI : article saved\nupdate save count
    else invalid ID
        controller.Controller --> view.UI : display unable to save
    end

else article already saved
    User -> view.UI : click unsave
    view.UI -> controller.Controller : removeArticle(articleId)
    controller.Controller --> view.UI : article removed
end

@enduml

```