# Configure Settings

## 1. Primary actor and goals
__User__: Wants to change location settings, either local or global. Wants to change appearance of app. Wants to view and/or change any other relevant account information.

## 2. Other stakeholders and their goals
No other stakeholders

## 3. Preconditions
* User is in Profile tab.
* User has clicked settings button.

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
|#implementation|Website System|

|User|
start
    repeat :Open profile tab;
        :Click settings;
        if (Change location) then (yes)
            if(Local) then (yes)
            (no) elseif (Global) then (yes)
            end if
            |Website System|
            :Execute __Location update__;
        (no)elseif (Update profile) then (yes)
            |User|
            if (Change picture) then (yes)
            elseif (Change bio) then (yes)
            endif
            |Website System|
            :Execute __Profile Update__;
        (no)elseif (Change appearance) then (yes)
            |Website System|
            :Execute __Appearance change__;

        endif
        |User|
        repeat while (Go back?) is (yes) not (no)

stop
@enduml
```