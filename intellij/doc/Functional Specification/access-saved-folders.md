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