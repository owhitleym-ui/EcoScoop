# Access Article 

## 1. Primary actor and goals
__User__: wants to obtain relevant articles on environment. Wants relevant, topical news and no outdated entries. Wants to read news in a readable format, with togglable display settings


## 2. Other stakeholders and their goals

* __Websites__: Want credits and attribution of original article. Want their page linked on hub. Want to attract readers. 
* __Author__: Wants credit for authoring article. Wants views, upvotes, and ratings on article.

## 3. Preconditions
* User is authenticated.
* User is in the Article hub tab.

## 4. Postconditions
* Article is saved to history.
* Tags are added to user preference
* Points are calculated and added to score after user finishes reading.
* Other articles are recommended.


## 5. Workflow

```plantuml
@startuml

skin rose

title Access Article (Casual Level)

'define the lanes
|#application|User|
|#implementation|System|

|User|
start
    if (Refresh) then (yes)
    elseif(Find Article)
    while (Open Article Tab) is (yes)
        :Click Article;
        :Read Article;
        |System|
        :Save to history;
        :Save preferences;
        |User|
        :Return;
    endwhile (no)
    endif
stop
@enduml
```


