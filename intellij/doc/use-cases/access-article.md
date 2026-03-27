# Access model.Article 

## 1. Primary actor and goals
__User__: Wants ease of access obtaining relevant articles concerning environmental news. Desires relevant topical news and new entries. Ease of access concerning reading format, with toggleable reader/display settings.

## 2. Other stakeholders and their goals

* __Websites__: Require credit and attribution of original article. Wants links and references on the article page. Attracting readers to their website for more content.

* __Author__: Require credit and attribution for writing the article. Wants to be able to see views, upvotes, and other ratings on articles.

## 3. Preconditions
* User is in the model.Article tab or has searched for article.

## 4. Postconditions
* model.Article is saved to history.
* Tags are added to user preference
* Points are calculated and added to score after user finishes reading.
* Other articles are recommended.


## 5. Workflow

```plantuml
@startuml

skin rose

title Access model.Article (Casual)

'define the lanes
|#application|User|
|#implementation|System|

start
|System|
:Load latest articles;

|User|
:Click model.Article;

|System|
:Load article content;

|User|
:Read model.Article;
if (React?) then (yes)
    :Execute __React Article__;
endif

|System|
if (User authenticated?) then (yes)
if (User wants to save article?) then (yes)
  :Execute __Save Article__;
endif
  :Save to history;
  :Calculate user points;
  :Recommend other articles;
  |User|
  if(Clicked suggested article) then (yes)
  |System|
  :Execute __Access Article__;
  endif
endif

|User|
:Return to model.Article Hub;

stop
@enduml
```
## Sequence Diagram - Load model.Article Database
```plantuml
@startuml
title Load model.Article Database
skin rose
hide footbox

participant controller.Controller as controller
participant model.ArticleRetriever as AR 

controller -> AR : getArticleList()

AR -> model.Article ** : creates

controller <-- AR : List <model.Article>

participant model.ArticleDatabase as AD

controller -> AD: save(List <model.Article>)

@enduml
```

## Sequence Diagram - Click on model.Article
```plantuml
@startuml

title Click on model.Article
skin rose
hide footbox

actor User as user
participant ScreenUI as view.UI
participant controller.Controller as controller
participant model.ArticleDatabase as AD


user -> view.UI : clickArticle(List[i])
view.UI -> controller : getArticle(List[i])
controller -> model.Article ** : getArticleData()


model.Article --> controller : getContent()
model.Article --> controller : id, author, url, 
controller --> view.UI :displayArticle(id, content)

participant UserProfile as UP

opt user react
user -> view.UI: reactToArticle(id)
ref over user, controller, view.UI,UP
    reactArticle(id)
    end ref
end

opt user save
user -> view.UI: saveToArticle(id)
ref over user, controller, view.UI,UP
    saveArticle(id)
    end ref
end

controller -> UP : calculatePoints()
controller -> view.UI : recommendArticles()
controller --> view.UI: List<model.Article>

user -> view.UI: return()


@enduml
```