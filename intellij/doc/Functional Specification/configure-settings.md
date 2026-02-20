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
    repeat :Click Settings;
        if (Change location) then (yes)
            if(Local) then (yes)
            (no) elseif (Global) then (yes)
            end if
            |System|
            :Update Location;
        (no)elseif (Update profile) then (yes)
            |User|
            if (Change picture) then (yes)
            elseif (Change bio) then (yes)
            endif
            |System|
            :Update Profile;
        (no)elseif (Change appearance) then (yes)
            |System|
            :Update App Appearance;

        endif
        |User|
        repeat while (Go back?) is (yes) not (no)

stop
@enduml
```