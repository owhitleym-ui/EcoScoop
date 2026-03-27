# View Profile

## 1. Primary actor and goals
__User__: Wants to check achievements, access stats and points, change bio, review tags. Wants clear direction of what to access.

## 2. Other stakeholders and their goals
No other stakeholders

## 3. Preconditions

* User opens EcoScoop
* User switches to Profile Section

## 4. Postconditions

* Displays the User Profile
* Displays Level/Gamified Aspects
* Shows Likes and Tags.

## 5. Workflow

```plantuml
@startuml

skin rose

title View Profile (Casual)

'define the lanes
|#application|User|
|#implementation|System|

|User|
start
:Click Profile Tab;
:Navigate to menu item;
|System|
switch (Handle User selection)
case ( Clicked Achievements?)
    |System|
    :Show number of articles read, 
    number of comments left, 
    stats on likes and dislikes;

case ( Clicked Level?)
    |System|
    :Show levels and point breakdown;

case ( Checked Tags?)
    repeat
    :Show most popular user tags;
    |System|
    switch (Chose tags?)
    |System|
    case ( Added model.Tag?)
    |System|
        :Save preferences and recommend more content with tag;
    case ( Removed model.Tag?)
    |System|
        :Save preferences and recommend less content with tag;
    case ( Kept Tags?)
    |System|
        :No changes to tags;
    endswitch
    repeat while (More tags?) is (yes) not (no)
endswitch



|User|
:Finish viewing profile;

stop
@enduml
```
## 6. Sequence Diagram
```plantuml
@startuml
skin rose
hide footbox
title View Profile (Sequence)

actor User
participant ": System view.UI" as view.UI
participant ": controller.Controller" as controller.Controller
participant ": model.Tag" as model.Tag
participant ": Profile" as Profile
participant ": model.ArticleRetriever" as Retriever

User -> view.UI : click profile
view.UI -> controller.Controller : displayProfile()
controller.Controller -> Profile : getStats()
Profile --> controller.Controller : return user stats
controller.Controller --> view.UI : show user stats

opt show achievements
User -> view.UI : click achievements
view.UI -> controller.Controller : displayAchievementScreen()
controller.Controller -> Profile : getAchievements()
Profile --> controller.Controller : return achievements
controller.Controller --> view.UI : show user achievement

else show level
User -> view.UI : click level
view.UI -> controller.Controller : displayLevel()
controller.Controller -> Profile : getLevel()
Profile --> controller.Controller : return user's levels
controller.Controller --> view.UI : show level stats

else show tags
User -> view.UI : click tags
view.UI -> controller.Controller : displayTags()
controller.Controller -> Retriever : getArticleTags()
Retriever --> controller.Controller : return tags
controller.Controller --> view.UI : show tags of liked articles

alt add tag
User -> view.UI : click add tag
view.UI -> controller.Controller : addTag()
controller.Controller -> model.Tag : addTag()
model.Tag --> controller.Controller : updates tags
controller.Controller --> view.UI : shows updated tags

else remove tag
User -> view.UI : click remove tag
view.UI -> controller.Controller : addTag()
controller.Controller -> model.Tag : removeTag()
model.Tag --> controller.Controller : updates tags
controller.Controller --> view.UI : shows updated tags
end alt


end opt
@enduml
```