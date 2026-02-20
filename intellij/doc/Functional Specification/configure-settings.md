# Configure Settings

## 1. Primary actor and goals
__User__: Ease of access chaning location settings, either local or global. Able to change the appearance of app through settings. Wants to view and/or change any relevant account information (i.e., profile picture, bio, etc.)

## 2. Other stakeholders and their goals
No other stakeholders

## 3. Preconditions
* User is authenticated
* User is in Profile tab.

## 4. Postconditions

* Profile/account information may be updated.
* Location information may be toggled.
* Appearance may be changed.

## 5. Workflow
```plantuml
@startuml

skin rose

title Configure Settings (Casual)

'define the lanes
|#application|User|
|#implementation|System|

|User|
start
repeat
    :Click Settings;

    if (Change Location?) then (Yes)
        if (Local?) then (Yes)
            |System|
            :Update Local Location;
        else (Global)
            |System|
            :Update Global Location;
        endif
    elseif (Update Profile?) then (Yes)
        if (Change Picture?) then (Yes)
            |System|
            :Update Profile Picture;
        elseif (Change Bio?) then (Yes)
            |System|
            :Update Bio;
        endif
    elseif (Change Appearance?) then (Yes)
        |System|
        :Update App Appearance;
    endif

|User|

repeat while (Go back to Settings?) is (Yes)

stop
@enduml
```