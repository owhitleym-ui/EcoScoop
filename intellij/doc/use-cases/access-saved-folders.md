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

title Sequence Diagram - Access Saved Folders

actor User
participant ": ScreenUI" as UI
participant ": Folder\nname, contents[]" as Folder
participant ": ArticleRetriever" as ArticleRetriever

User -> UI : click folder
UI -> Folder : open()

alt name = "History"
    Folder -> ArticleRetriever : fetchHistory()
    ArticleRetriever --> Folder : articles (newest→oldest)
    Folder --> UI : display contents
else other folder
    Folder --> UI : return contents[]
end

UI --> User : show folder contents

opt user confirms folder choice

' Rename
User -> UI : rename folder
UI -> Folder : edit(newName)
UI --> User : show updated name

' Add article
User -> UI : add article to folder
UI -> Folder : addArticle(article)
Folder --> UI : updated contents[]

' Remove article
User -> UI : remove article
UI -> Folder : removeArticle(id)
Folder --> UI : updated contents[]

' Delete folder
    User -> UI : delete folder
    UI -> Folder : delete()
    Folder --> UI : folder removed
    UI --> User : return to folder list

User -> UI : open article
UI -> ArticleRetriever : executeAccessArticle(id)
ArticleRetriever --> Folder : update history log
ArticleRetriever --> UI : render article
UI --> User : article displayed

end


@enduml
```