# Access History

## 1. Primary actor and goals

__User__: Wants to look through previous articles that they have read. Ease of access rereading articles, accessing history, and previous reactions

## 2. Other stakeholders and their goals
* No other stakeholders.

## 3. Preconditions
* User is authenticated
* User switches to view profile tab.
* User clicks settings tab.

## 4. Postconditions
* History is accessed and user knows which articles they have read.
* User is able to access an article from the history tab

## 5. Workflow
```plantuml
@startuml

skin rose

title Access History (Casual)
'define the lanes
|#application|User|
|#implementation|System|

|User|
start
    repeat :Execute __View Profile__;
        :Click Settings;
        |User|
        :Click History;
        |System|
        :Take to updated history of articles;
        |User|
        :Choose article;
        |System|
        :Execute __Access Article__;
        :Update article history;
        |User|
        repeat while (Go back to profile?) is (yes) not (no)

stop
@enduml
```