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
:Clicks profile tab;

if (Click achievements) then (yes)
    |System|
    :Show Articles read;
    :Show Comments left;
    :Show Likes and Dislikes;
    
(no) elseif (Click on points) then (yes)
    |System|
    :See levels and breakdown;
(no) elseif  (Check tags) then (yes)
:Show most popular user tags;
    |User|
    if (Add tag) then (yes)
        |System|
        :Save preferences and recommend more of tag;
    (no) elseif (Remove tag) then (yes)
        |System|
        :Save preferences and recommend less of tag;
    (no)elseif (Keep tags) then (yes)
    endif

endif
stop
@enduml
```