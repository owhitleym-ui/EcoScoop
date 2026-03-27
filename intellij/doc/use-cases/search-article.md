# Search model.Article

## 1. Primary actor and goals
__User__: Wants to look for relevant articles depending on keywords, tags, authors, or publishing year. Looking for relevant, topical news that all relate to what the user inputs and is searching for.

## 2. Other stakeholders and their goals

* __Websites__: Wants information about if their article was searched and accessed.
* __Author__: Wants information if their article was searched for.


## 3. Preconditions
* User switches to model.Article Section

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
repeat 
if (Click on Search Tab) then (yes)
|System|
    if (Keyword Search?) then (yes)
    :Search by Keywords;
    (no) elseif (model.Tag Search?) then (yes)
    :Search by Tags;
    (no) elseif (model.Author Search?) then (yes)
    :Search by model.Author;
    endif

endif
|ESS|
:Load Articles by Search;
|User|
:View List;
|System|
    
    if (Trending?) then (yes)
    :Sort by most viewed articles;
    (no) elseif (Rating?) then (yes)
    :Sort by Rating;
    (no) elseif (Sort by Date?) then (yes)
    :Sort by Publishing Date;
    (default) elseif (Sort by Relevance?) then (yes)
    endif

|ESS|
:Sort loaded Articles;

|User|

repeat while (Finish Searching?) is (no) not (yes)
:Click article;

|System|
:Execute __Access Article__;
stop
@enduml
```
## 6. Sequence Diagram
```plantuml
@startuml
skin rose
hide footbox
title Search model.Article (Sequence)

actor User
participant ": System view.UI" as view.UI
participant ": SearchController" as controller.Controller
participant ": model.ArticleDatabase" as Database

User -> view.UI : open search tab
User -> view.UI : enter search input\n(keyword / tag / author / year)
view.UI -> controller.Controller : submitSearch(query, type)

ref over Database 
loadArticleDatabase
end ref

controller.Controller -> Database : loadArticles(query, type)
Database --> controller.Controller : return matching articles

controller.Controller --> view.UI : display results
User -> view.UI : choose sort criteria\n(relevance / date / rating / trending)
view.UI -> controller.Controller : sortArticles(criteria)
controller.Controller --> view.UI : display sorted results


User -> view.UI : select article
ref over view.UI, controller.Controller
  Access model.Article
end ref

@enduml

```