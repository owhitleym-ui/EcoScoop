# Access Article 

## 1. Primary actor and goals
__User__: Wants ease of access obtaining relevant articles concerning environmental news. Desires relevant topical news and new entries. Ease of access concerning reading format, with toggleable reader/display settings.

## 2. Other stakeholders and their goals

* __Websites__: Require credit and attribution of original article. Wants links and references on the article page. Attracting readers to their website for more content.

* __Author__: Require credit and attribution for writing the article. Wants to be able to see views, upvotes, and other ratings on articles.

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
:Return to Article Hub;

stop
@enduml
```


