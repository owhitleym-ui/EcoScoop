# Search Article

## 1. Primary actor and goals
__User__: Wants to look for relevant articles depending on keywords, tags, authors, or publishing year. Looking for relevant, topical news that all relate to what the user inputs and is searching for.

## 2. Other stakeholders and their goals

* __Websites__: Wants information about if their article was searched and accessed.
* __Author__: Wants information if their article was searched for.


## 3. Preconditions
* User switches to Article Section

## 4. Postconditions
* List of relevant articles are shown
* Ordered from most relevant based on relevancy from keywords

## 5. Workflow
```plantuml
@startuml
skin rose

title Search Articles (Casual)

'define the lanes
|#application|User|
|#implementation|System|
|#technology|ESS|

|User|
start
if (Click on Search Tab) then (yes)
|System|
    if (Keyword Search?) then (yes)
    :Search by Keywords;
    (no) elseif (Tag Search?) then (yes)
    :Search by Tags;
    (no) elseif (Author Search?) then (yes)
    :Search by Author;
    (no) elseif (Publishing Date Search?) then (yes)
    :Search by Publishing Date;
    endif
:Return List;
endif
|User|
:View List;
:Click article;

|System|

stop
@enduml
```