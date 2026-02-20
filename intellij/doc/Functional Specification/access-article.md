# Access Article 

## 1. Primary actor and goals
__User__: Wants ease of access obtaining relevant articles concerning environmental news. Desires relevant topical news and new entries. Ease of access concerning reading format, with toggleable reader/display settings.

## 2. Other stakeholders and their goals

* __Websites__: Require credit and attribution of original article. Wants links and references on the article page. Attracting readers to their website for more content.

* __Author__: Require credit and attribution for writing the article. Wants to be able to see views, upvotes, and other ratings on articles.

## 3. Preconditions
* User is in the Article tab.

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

|User|
start
    if (Refresh) then (yes)
    elseif(Find Article)
    while (Open Article Tab) is (yes)
        :Execute __Search Article__;
        :Click Article;
        :Read Article;
        |System|
        if (Is User Authenticated?) then ( yes)
        :Save to history;
        :Save preferences;
        :Calculates User Points;
        :Recommends other articles;
        endif
        |User|
        :Return;
    endwhile (no)
    endif
stop
@enduml
```


