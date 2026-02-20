# Configure Settings

## 1. Primary actor and goals
__User__: Ease of access changing location settings, either local or global. Able to change the appearance of app through settings. Wants to view and/or change any relevant account information (i.e., profile picture, bio, etc.)

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

    if (Change Location?) then (yes)
        if (Local?) then (yes)
            |System|
            :Update Local Location;
        else (global)
            |System|
            :Update Global Location;
        endif
    (no)elseif (Update Profile?) then (yes)
        if (Change Picture?) then (yes)
            |System|
            :Update Profile Picture;
        (no)elseif (Change Bio?) then (yes)
            |System|
            :Update Bio;
        endif
    (no)elseif (Change Appearance?) then (yes)
        |System|
        :Update App Appearance;
    endif

|User|

repeat while (Go back to Profile?) is (yes)

stop
@enduml
```