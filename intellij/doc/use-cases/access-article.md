# Access Article 

## 1. Primary actor and goals
__User__: Wants ease of access obtaining relevant articles concerning environmental news. Desires relevant topical news and new entries. Ease of access concerning reading format, with toggleable reader/display settings.

## 2. Other stakeholders and their goals

* __Websites__: Require credit and attribution of original article. Wants links and references on the article page. Attracting readers to their website for more content.

* __Author__: Require credit and attribution for writing the article. Wants to be able to see s, upvotes, and other ratings on articles.

## 3. Preconditions
* User is in the Article tab or has searched for article.

## 4. Postconditions
* Article is saved to history.
* Tags are added to user preference
* Points are calculated and added to score after user finishes reading.
* Other articles are recommended.


## 5. Workflow

```plantuml
@startuml

skin rose

title Access Article (Casual)

'define the lanes
|#application|User|
|#implementation|System|

start
|System|
:Load latest articles;

|User|
:Click Article;

|System|
:Load article content;

|User|
:Read Article;
if (React?) then (yes)
    :Execute __React Article__;
endif

|System|
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

|User|
:Return to Article Hub;

stop
@enduml
```
## Sequence Diagram - Load Article Database
```plantuml
@startuml
title Load Article Database
skin rose
hide footbox

participant ": Controller" as controller

create participant "ar : ArticleRetriever" as AR
controller -> AR : ar = new ArticleRetriever()

create participant "ad : ArticleDatabase" as AD
AR -> AD : ad = new ArticleDatabase()

create participant "fm : FolderManager" as FM
AR -> FM : fm = new FolderManager(this)

AR -> AD : getDatabase()

create participant "f : FeedFetcher" as FF
AD -> FF : f = new FeedFetcher()
AD -> FF : fetchAll(feeds)

loop for each feed (Grist, Carbon Brief)
  create participant "ap : ArticleParser" as AP
  FF -> AP : ap = new ArticleParser()
  FF -> AP : parse(args, xml, siteName)

  create participant "s : Source" as S
  AP -> S : s = new Source(website, url, pubDate)

  create participant "a : Author" as A
  AP -> A : a = new Author(name)

  create participant "t : Tag" as T
  AP -> T : t = new Tag(category)

  create participant "art : Article" as ART
  AP -> ART : art = new Article(id, title, desc, authors, tags, source, content)

  FF <-- AP : loadArticles() : List<Article>
end

AD <-- FF : List<Article>

loop for each article
  AD -> AD : database.put(article.getId(), article)
end

AR <-- AD : HashMap<Integer, Article>

controller <-- AR : retriever ready

@enduml
```

## Sequence Diagram - Click on Article
```plantuml
@startuml

title Click on Article (Access Article)
skin rose
hide footbox

actor User as user
participant "ui : CmdLineUI" as UI
participant "controller : Controller" as controller
participant "ar : ArticleRetriever" as AR
participant "art : Article" as article

user -> UI : enters article ID
UI -> controller : onGetArticle(id)
controller -> AR : getArticle(id)
AR --> controller : art : Article
controller -> UI : runDisplayArticle(art)
UI -> article : printArticle()
article --> UI : formatted article string
UI --> user : displays article with likes/dislikes/comments

opt user reacts
  ref over user, UI, controller, AR, article
    React Article(id)
  end ref
end

opt user saves
  ref over user, UI, controller, AR
    Save Article(articleId, folderName)
  end ref
end

opt user searches
  ref over user, UI, controller, AR
    Search Articles(query, type)
  end ref
end

user -> UI : return to article list

@enduml
```