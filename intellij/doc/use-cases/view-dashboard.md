# View Eco Dashboard

## 1. Primary actor and goals

__User__: Wants to access quick, relevant info on sustainability stats and current information on the environment. Wants to be able to read the charts and click on them for more interactivity.

## 2. Other stakeholders and their goals

* __Sources__: Want credit for data used.


## 3. Preconditions

* Daily information has been compressed into easily digestible graphics.
* User has opened the app or switched back into Eco Dashboard tab.

## 4. Postconditions

* User has quick and easy access to stats.
* User is able to switch data interactive view until satisfied.

## 5. Workflow

```plantuml
@startuml

skin rose

title View Dashboard (Casual)

'define the lanes
|#application|User|
|#implementation|System|
|#technology|ESS|

start

|ESS|
:Retrieve environmental statistics;


|System|
switch (Data Display)
    case ( Fossil Fuel Data)
        |System|
        :Display detailed fossil fuel stats;
    case ( Energy Consumption Data)
        |System|
        :Display detailed energy usage stats;
    case (   Air Emissions)
        |System|
        :Show air quality trends and stats;
    case ( Waste Data)
        |System|
        :Display waste management statistics;
    case ( Water Data)
        |System|
        :Show water consumption trends;

endswitch

repeat

if ( Interactive View) then (yes)
:Enable extended interactive view;
|User|
:Click environmental visual;

|System|
:Display specific visual;
(no) elseif ( model.Article View) then (yes)
:Execute __Search Article__;

(no) elseif (Profile View) then (yes)
:Execute __View Profile__;
endif

repeat while (Switch tab?) is (yes) not (no)


stop
@enduml
```

## 6. Sequence Diagram
```plantuml
@startuml
skin rose
hide footbox
title View Dashboard (Sequence)

actor User
participant ": System view.UI" as view.UI
participant ": controller.Controller" as controller.Controller
participant ": Dashboard" as Dashboard
participant ": ESS" as ESS
participant ": Diagram" as Diagram

User -> view.UI : click dashboard tab
view.UI -> controller.Controller : loadDashboard()
controller.Controller -> ESS : fetchEnvironmentalStats()
ESS --> controller.Controller : return raw stats
controller.Controller -> Dashboard : processStats(rawStats)
Dashboard --> controller.Controller : return processed stats
controller.Controller -> Diagram : generateVisuals(stats)
Diagram --> controller.Controller : return visuals
controller.Controller --> view.UI : display dashboard overview

alt view fossil fuels
    User -> view.UI : click fossil fuels
    view.UI -> controller.Controller : loadDetail(fossilFuels)
    controller.Controller -> Diagram : getDetailedVisual(fossilFuels)
    Diagram --> controller.Controller : return expanded visual
    controller.Controller --> view.UI : show fossil fuel stats

else view energy usage
    User -> view.UI : click energy usage
    view.UI -> controller.Controller : loadDetail(energy)
    controller.Controller -> Diagram : getDetailedVisual(energy)
    Diagram --> controller.Controller : return expanded visual
    controller.Controller --> view.UI : show energy usage stats

else view air emissions
    User -> view.UI : click air emissions
    view.UI -> controller.Controller : loadDetail(airEmissions)
    controller.Controller -> Diagram : getDetailedVisual(airEmissions)
    Diagram --> controller.Controller : return expanded visual
    controller.Controller --> view.UI : show air emissions stats

else view waste data
    User -> view.UI : click waste
    view.UI -> controller.Controller : loadDetail(waste)
    controller.Controller -> Diagram : getDetailedVisual(waste)
    Diagram --> controller.Controller : return expanded visual
    controller.Controller --> view.UI : show waste stats

else view water data
    User -> view.UI : click water
    view.UI -> controller.Controller : loadDetail(water)
    controller.Controller -> Diagram : getDetailedVisual(water)
    Diagram --> controller.Controller : return expanded visual
    controller.Controller --> view.UI : show water stats
end
User -> view.UI : switch to article view

ref over view.UI, controller.Controller
    Search model.Article
end ref

User -> view.UI : switch to profile view

ref over view.UI, controller.Controller
    View Profile
end ref

@enduml
```