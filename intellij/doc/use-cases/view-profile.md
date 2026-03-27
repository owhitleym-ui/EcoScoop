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
    case ( Added Tag?)
    |System|
        :Save preferences and recommend more content with tag;
    case ( Removed Tag?)
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
participant ": System UI" as UI
participant ": Controller" as Controller
participant ": Tag" as Tag
participant ": Profile" as Profile
participant ": ArticleRetriever" as Retriever

User -> UI : click profile
UI -> Controller : displayProfile()
Controller -> Profile : getStats()
Profile --> Controller : return user stats
Controller --> UI : show user stats

opt show achievements
User -> UI : click achievements
UI -> Controller : displayAchievementScreen()
Controller -> Profile : getAchievements()
Profile --> Controller : return achievements
Controller --> UI : show user achievement

else show level
User -> UI : click level
UI -> Controller : displayLevel()
Controller -> Profile : getLevel()
Profile --> Controller : return user's levels
Controller --> UI : show level stats

else show tags
User -> UI : click tags
UI -> Controller : displayTags()
Controller -> Retriever : getArticleTags()
Retriever --> Controller : return tags
Controller --> UI : show tags of liked articles

alt add tag
User -> UI : click add tag
UI -> Controller : addTag()
Controller -> Tag : addTag()
Tag --> Controller : updates tags
Controller --> UI : shows updated tags

else remove tag
User -> UI : click remove tag
UI -> Controller : addTag()
Controller -> Tag : removeTag()
Tag --> Controller : updates tags
Controller --> UI : shows updated tags
end alt


end opt
@enduml
```