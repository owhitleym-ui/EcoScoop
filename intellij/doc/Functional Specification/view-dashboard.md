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

## 5. Workflow

```plantuml
@startuml

skin rose

title View Dashboard (Casual)

'define the lanes
|#application|User|
|#implementation|System|

|User|
start
:Open Eco Dashboard;

switch (Select Dashboard Item)
    case (Fossil Fuel Data)
        |User|
        :Click Fossil Fuel Chart;
        |System|
        :Display detailed fossil fuel stats;
    case (Energy Consumption Data)
        |User|
        :Click Energy Consumption Chart;
        |System|
        :Display detailed energy usage stats;
    case (Air Emissions)
        |User|
        :Click Air Emissions Chart;
        |System|
        :Show air quality trends and stats;
    case (Waste Data)
        |User|
        :Click Waste Chart;
        |System|
        :Display waste management statistics;
    case (Water Data)
        |User|
        :Click Water Usage Chart;
        |System|
        :Show water consumption trends;
endswitch

|System|
:Enable extended interactive view;
|User|
:Switch between charts as needed;

stop
@enduml
```