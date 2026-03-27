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

start
repeat
    #application:Click Settings;

    #application:if (Change Location?) then (yes)
        #application:if (Local?) then (yes)
        #application:Provide local information;
            #implementation:Update Local Location;
        else (global)
            #application:Provide global information;
            #implementation:Update Global Location;
        endif
    #application:(no)elseif (Update Profile?) then (yes)
        #application:if (Change Picture?) then (yes)
            #application:Provide new profile picture;
            #implementation:Update Profile Picture;
        #application:(no)elseif (Change Bio?) then (yes)
            #application:Provide updated Bio;
            #implementation:Update Bio;
        endif
    #application:(no)elseif (Change Appearance?) then (yes)
        #application:Select new App Appearance;
        #implementation:Update App Appearance;
    endif

repeat while(Go back to Profile?) is (yes)

stop
@enduml
```

## 6. Sequence Diagram
```plantuml
@startuml
skin rose
hide footbox
title Configure Settings (Sequence)

actor User
participant ": System view.UI" as view.UI
participant ": controller.Controller" as controller.Controller
participant ": Profile" as Profile

User -> view.UI : click settings
view.UI -> controller.Controller : loadSettings()
controller.Controller -> Profile : getProfileData()
Profile --> controller.Controller : return profile data
controller.Controller --> view.UI : display settings

User -> view.UI : modify settings
view.UI -> controller.Controller : saveSettings(settingsData)
controller.Controller -> Profile : update(settingsData)
Profile --> controller.Controller : confirmed
controller.Controller --> view.UI : refresh display

User -> view.UI : go back to profile
view.UI -> controller.Controller : loadProfile()
controller.Controller -> Profile : getProfileData()
Profile --> controller.Controller : return profile data
controller.Controller --> view.UI : display profile

@enduml
```