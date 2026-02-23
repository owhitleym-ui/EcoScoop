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
