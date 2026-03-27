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
repeat 
if (Click on Search Tab) then (yes)
|System|
    if (Keyword Search?) then (yes)
    :Search by Keywords;
    (no) elseif (Tag Search?) then (yes)
    :Search by Tags;
    (no) elseif (Author Search?) then (yes)
    :Search by Author;
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
title Search by Keyword

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR
participant "art : Article" as article

user -> UI : selects "Search Articles"
UI -> user : display search type menu\n(0. Return / 1. Keyword / 2. Tag / 3. Author)
user -> UI : enters 1
UI -> user : "Enter search query: "
user -> UI : enters query
UI -> controller : onSearchQuery(query, "keyword")
controller -> AR : searchArticles(query, "keyword")

loop for each article in articleList
  AR -> article : getTitle(), getDescription(), getContent()
  AR -> AR : count keyword hits in title,\ndescription, and content
end

AR -> AR : sort results by keyword hit count (descending)
AR --> controller : List<Article>
controller --> UI : results
UI --> user : display search results (N found)

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Search by Tag

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR
participant "art : Article" as article
participant "t : Tag" as tag

user -> UI : selects "Search Articles"
UI -> user : display search type menu\n(0. Return / 1. Keyword / 2. Tag / 3. Author)
user -> UI : enters 2
UI -> user : "Enter search query: "
user -> UI : enters query
UI -> controller : onSearchQuery(query, "tag")
controller -> AR : searchArticles(query, "tag")

loop for each article in articleList
  AR -> article : getTagList()
  loop for each tag
    AR -> tag : getName()
    AR -> AR : check if tag name contains query
  end
end

AR --> controller : List<Article>
controller --> UI : results
UI --> user : display search results (N found)

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Search by Author

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR
participant "art : Article" as article
participant "a : Author" as author

user -> UI : selects "Search Articles"
UI -> user : display search type menu\n(0. Return / 1. Keyword / 2. Tag / 3. Author)
user -> UI : enters 3
UI -> user : "Enter search query: "
user -> UI : enters query
UI -> controller : onSearchQuery(query, "author")
controller -> AR : searchArticles(query, "author")

loop for each article in articleList
  AR -> article : getAuthors()
  loop for each author
    AR -> author : getName()
    AR -> AR : check if author name contains query
  end
end

AR --> controller : List<Article>
controller --> UI : results
UI --> user : display search results (N found)

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Sort Search Results

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR


UI -> user : display sort menu\n(0. Skip / 1. Relevance / 2. Date / 3. Rating / 4. Trending)
user -> UI : enters sort choice
UI -> controller : onSortResults(results, criteria)
controller -> AR : sortArticles(results, criteria)

alt criteria = "date"
  AR -> AR : sort by publishDate (newest first)
else criteria = "rating"
  note right : Not yet implemented
else criteria = "trending"
  note right : Not yet implemented
else criteria = "relevance" (default)
  note right : Not yet implemented
end

AR --> controller : List<Article> (sorted)
controller --> UI : sorted results
UI --> user : display sorted search results

@enduml
```
```plantuml
@startuml
skin rose
hide footbox
title Open Article from Search Results

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR

user -> UI : select article from results
ref over user, UI, controller, AR
  Access Article(id)
end ref

@enduml
```