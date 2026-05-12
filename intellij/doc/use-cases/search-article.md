# Search Article

## 1. Primary actor and goals
__User__: Wants to find relevant environmental articles by keyword, tag, or author, and sort results in a meaningful order.

## 2. Other stakeholders and their goals

* __Websites__: Want attribution when their articles are surfaced.
* __Authors__: Want visibility when their articles are searched.

## 3. Preconditions
* User navigates to the Search tab.

## 4. Postconditions
* A list of matching articles is displayed, ordered by the selected sort criterion.

## 5. Workflow
```plantuml
@startuml
skin rose

title Search Articles (Casual)

|#application|User|
|#implementation|System|

|User|
start
:Click Search Tab;

|System|
if (Keyword Search?) then (yes)
  :Search by keywords;\nRank by hit count;
(no) elseif (Tag Search?) then (yes)
  :Search by tag;
(no) elseif (Author Search?) then (yes)
  :Search by author name;
endif

:Show results list;

|User|
if (Sort results?) then (yes)
  |System|
  if (Relevance?) then (yes)
    :Restore original search order;
  (no) elseif (Oldest First?) then (yes)
    :Sort by publishDate ascending;
  (no) elseif (Source A-Z?) then (yes)
    :Sort alphabetically by source;
  endif
endif

|User|
:Click article;

|System|
:Execute __Access Article__;
stop
@enduml
```

## 6. Sequence Diagrams

```plantuml
@startuml
skin rose
hide footbox
title Search by Keyword

actor User as user
participant "fragment : SearchArticleFragment" as UI
participant "activity : ControllerActivity" as controller
participant "ar : ArticleRetriever" as AR
participant "art : Article" as article

user -> UI : selects Keyword chip, enters query, taps Search
UI -> controller : onSearchQuery(query, "keyword", ui)
controller -> AR : searchArticles(query, "keyword")

loop for each article in database
  AR -> article : getTitle(), getDescription(), getContent()
  AR -> AR : count keyword hits
end

AR -> AR : sort by hit count descending
AR --> controller : List<Article>
controller -> UI : runShowFreshResults(results)
UI --> user : display results (N found), Relevance chip selected

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Search by Tag

actor User as user
participant "fragment : SearchArticleFragment" as UI
participant "activity : ControllerActivity" as controller
participant "ar : ArticleRetriever" as AR
participant "t : Tag" as tag

user -> UI : selects Tag chip, enters query, taps Search
UI -> controller : onSearchQuery(query, "tag", ui)
controller -> AR : searchArticles(query, "tag")

loop for each article in database
  loop for each tag
    AR -> tag : getName()
    AR -> AR : check if tag contains query
  end
end

AR --> controller : List<Article>
controller -> UI : runShowFreshResults(results)
UI --> user : display results

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Search by Author

actor User as user
participant "fragment : SearchArticleFragment" as UI
participant "activity : ControllerActivity" as controller
participant "ar : ArticleRetriever" as AR
participant "a : Author" as author

user -> UI : selects Author chip, enters query, taps Search
UI -> controller : onSearchQuery(query, "author", ui)
controller -> AR : searchArticles(query, "author")

loop for each article in database
  loop for each author
    AR -> author : getName()
    AR -> AR : check if author name contains query
  end
end

AR --> controller : List<Article>
controller -> UI : runShowFreshResults(results)
UI --> user : display results

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Sort Search Results

actor User as user
participant "fragment : SearchArticleFragment" as UI
participant "activity : ControllerActivity" as controller
participant "ar : ArticleRetriever" as AR

user -> UI : selects sort chip
UI -> UI : check selected chip id

alt Relevance chip
  UI -> UI : runShowResults(originalResults)
else Oldest First chip
  UI -> controller : onSortResults(originalResults, "oldest", ui)
  controller -> AR : sortArticles(results, "oldest")
  AR -> AR : sort by publishDate ascending
  AR --> controller : sorted List<Article>
  controller -> UI : runShowResults(sorted)
else Source A-Z chip
  UI -> controller : onSortResults(originalResults, "source", ui)
  controller -> AR : sortArticles(results, "source")
  AR -> AR : sort alphabetically by source name
  AR --> controller : sorted List<Article>
  controller -> UI : runShowResults(sorted)
end

UI --> user : display sorted results

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Open Article from Search Results

actor User as user
participant "fragment : SearchArticleFragment" as UI
participant "activity : ControllerActivity" as controller

user -> UI : taps article card
UI -> controller : onArticleClicked(id)

ref over user, UI, controller
  Access Article(id)
end ref

@enduml
```
