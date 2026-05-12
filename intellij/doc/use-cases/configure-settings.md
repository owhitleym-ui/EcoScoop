# Configure Settings

## 1. Primary actor and goals
__User__: Wants to change their unit preference (metric vs. imperial) and their location mode (use device GPS vs. a saved global city) from the Profile screen.

## 2. Other stakeholders and their goals
No other stakeholders.

## 3. Preconditions
* User is authenticated.
* User is on the Profile tab.

## 4. Postconditions
* Unit system preference is saved to the User model and the dashboard re-renders in the selected units.
* Location mode preference is saved to the User model and the dashboard re-fetches weather for the appropriate location.

## 5. Workflow
```plantuml
@startuml
skin rose

title Configure Settings (Casual)

|#application|User|
|#implementation|System|

|User|
start
:Tap Profile Tab;
:Tap Settings;

|System|
:Load current preferences\n(useMetric, useLocalLocation);
:Display settings toggles;

|User|
if (Change unit system?) then (yes)
  :Toggle Metric / Imperial switch;
  |System|
  :Update User.useMetric;
  :Re-render dashboard in new units;
  |User|
endif

if (Change location mode?) then (yes)
  :Toggle Local / Global switch;
  |System|
  :Update User.useLocalLocation;
  if (Local selected?) then (yes)
    :Re-fetch weather using device GPS;
  else (global)
    :Re-fetch weather using saved city;
  endif
  |User|
endif

:Navigate back to Profile;
stop
@enduml
```

## 6. Sequence Diagrams

```plantuml
@startuml
skin rose
hide footbox
title Change Unit System (Metric / Imperial)

actor User as user
participant "fragment : ProfileFragment" as UI
participant "activity : ControllerActivity" as controller
participant "u : User" as userModel
participant "fragment2 : DashboardFragment" as dash

user -> UI : toggles Metric switch
UI -> controller : onSettingChanged(useMetric=true, useLocalLocation=...)
controller -> userModel : setUseMetric(true)
controller -> dash : setUseMetric(true)
controller -> dash : onWeatherLoaded(retriever)
dash --> user : dashboard re-renders in new units

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Change Location Mode (Local / Global)

actor User as user
participant "fragment : ProfileFragment" as UI
participant "activity : ControllerActivity" as controller
participant "u : User" as userModel
participant "fetcher : EcoDataFetcher" as fetcher
participant "fragment2 : DashboardFragment" as dash

user -> UI : toggles Local / Global switch
UI -> controller : onSettingChanged(useMetric=..., useLocalLocation=true)
controller -> userModel : setUseLocalLocation(true)

alt Local selected
  controller -> controller : requestFreshLocation()
  note right : uses device GPS
else Global selected
  controller -> controller : geocodeAndFetch(savedCity)
  note right : uses saved city name
end

controller -> fetcher : fetch(lat, lon)
fetcher --> controller : WeatherData
controller -> dash : onWeatherLoaded(retriever)
dash --> user : dashboard updates for new location

@enduml
```
