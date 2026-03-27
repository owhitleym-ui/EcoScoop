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

title Access Saved Folders (Sequence)

actor User
participant ": ScreenUI" as view.UI
participant ": model.Folder\nname, contents[]" as model.Folder
participant ": model.ArticleRetriever" as model.ArticleRetriever

User -> view.UI : click folder
view.UI -> model.Folder : open()

alt name = "History"
    model.Folder -> model.ArticleRetriever : fetchHistory()
    model.ArticleRetriever --> model.Folder : articles (newest→oldest)
    model.Folder --> view.UI : display contents
else other folder
    model.Folder --> view.UI : return contents[]
end

view.UI --> User : show folder contents

opt user confirms folder choice

' Rename
User -> view.UI : rename folder
view.UI -> model.Folder : edit(newName)
view.UI --> User : show updated name

' Add article
User -> view.UI : add article to folder
view.UI -> model.Folder : addArticle(article)
model.Folder --> view.UI : updated contents[]

' Remove article
User -> view.UI : remove article
view.UI -> model.Folder : removeArticle(id)
model.Folder --> view.UI : updated contents[]

' Delete folder
    User -> view.UI : delete folder
    view.UI -> model.Folder : delete()
    model.Folder --> view.UI : folder removed
    view.UI --> User : return to folder list

User -> view.UI : open article
view.UI -> model.ArticleRetriever : executeAccessArticle(id)
model.ArticleRetriever --> model.Folder : update history log
model.ArticleRetriever --> view.UI : render article
view.UI --> User : article displayed

end


@enduml
```