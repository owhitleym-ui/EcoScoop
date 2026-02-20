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

if (Click Achievements?) then (yes)
    |System|
    :Execute __Access History__;
    :Show articles read;
    :Show comments left;
    :Show likes and dislikes;

elseif (Click Points?) then (yes)
    |System|
    :Show levels and point breakdown;

elseif (Check Tags?) then (yes)
    |System|
    :Show most popular user tags;
    |User|
    if (Add Tag?) then (yes)
        |System|
        :Save preferences and recommend more content with tag;
    elseif (Remove Tag?) then (yes)
        |System|
        :Save preferences and recommend less content with tag;
    elseif (Keep Tags?) then (yes)
        |System|
        :No changes to tags;
    endif
endif

|User|
:Finish viewing profile;

stop
@enduml
```