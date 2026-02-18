# Access History

## 1. Primary actor and goals

__User__: Wants to look through previous articles read. Wants ease of access to react, or reread article.

## 2. Other stakeholders and their goals
* No other stakeholders.

## 3. Preconditions
* User switches to view profile tab.
* User clicks settings tab.

## 4. Postconditions
* History is accessed and user knows which articles they have read.

## 5. Workflow
```plantuml
@startuml

skin rose

title Access History (Casual Level)
'define the lanes
|#application|User|
|#implementation|System|

|User|
start
    repeat :Open profile;
        :Click settings;
        :Click history;
        |System|
        :Fetch articles from stored file;
        |User|
        :Access article;
        :Display article;
        repeat while (Go back?) is (yes) not (no)

stop
@enduml
```