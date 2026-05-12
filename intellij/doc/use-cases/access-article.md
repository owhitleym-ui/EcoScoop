# Access Article

## 1. Primary actor and goals
__User__: Wants to read a full article, react to it, and optionally save it to a folder — all from a single screen.

## 2. Other stakeholders and their goals

* __Websites__: Require attribution. A link to the original article is shown.
* __Authors__: Require credit. Author names are displayed on the article screen.

## 3. Preconditions
* User is on the Article Feed or Search results screen and taps a card.

## 4. Postconditions
* Full article content is displayed.
* User may like/dislike, comment, or save the article.
* User can navigate back to the feed or search.

## 5. Workflow
```plantuml
@startuml
skin rose

title Access Article (Casual)

|#application|User|
|#implementation|System|

start

|System|
:Load article list from ArticleRepository;

|User|
:Tap article card;

|System|
:Look up Article by UUID;
:Display title, authors, source,\ndate, tags, content, image;

|User|
if (React?) then (yes)
  :Execute __React Article__;
endif
if (Save?) then (yes)
  :Execute __Save Article__;
endif
:Return to feed or search;

stop
@enduml
```

## Sequence Diagrams

```plantuml
@startuml
skin rose
hide footbox
title Load Article Database on App Start

participant "activity : ControllerActivity" as controller

create participant "ar : ArticleRetriever" as AR
controller -> AR : new ArticleRetriever(database)

create participant "repo : ArticleRepository" as repo
controller -> repo : new ArticleRepository(feeds)

create participant "ff : FeedFetcher" as FF
repo -> FF : new FeedFetcher()
repo -> FF : fetchAll(feeds)

loop for each feed (Grist, Carbon Brief, Earth911)
  create participant "ap : ArticleParser" as AP
  FF -> AP : new ArticleParser()
  FF -> AP : parse(xml, siteName)

  create participant "s : Source" as S
  AP -> S : new Source(website, url, pubDate)

  create participant "a : Author" as A
  AP -> A : new Author(name)

  create participant "t : Tag" as T
  AP -> T : new Tag(category)

  create participant "art : Article" as ART
  AP -> ART : new Article(uuid, title, desc, authors, tags, source, content, imageUrl)

  FF <-- AP : loadArticles() : List<Article>
end

repo <-- FF : List<Article>
controller <-- repo : ArticleRepository ready

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Tap Article Card

actor User as user
participant "fragment : ArticleFeedFragment" as feed
participant "activity : ControllerActivity" as controller
participant "ar : ArticleRetriever" as AR
participant "fragment2 : DisplayArticleFragment" as display
participant "art : Article" as article

user -> feed : taps article card
feed -> controller : onArticleClicked(id)
controller -> AR : getArticle(id)
AR --> controller : art : Article
controller -> display : showArticle(art)
display -> article : getTitle(), getContent(), getAuthors(), getSource(), getLikes(), getComments()
display --> user : full article screen with reaction buttons

opt user reacts
  ref over user, display, controller
    React Article(id)
  end ref
end

opt user saves
  ref over user, display, controller
    Save Article(id, folderName)
  end ref
end

user -> display : taps Back
display --> user : return to article feed or search

@enduml
```
