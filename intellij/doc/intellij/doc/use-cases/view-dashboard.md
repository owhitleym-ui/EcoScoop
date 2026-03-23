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
(no) elseif ( Article View) then (yes)
:Execute __Search Article__;

(no) elseif (Profile View) then (yes)
:Execute __View Profile__;
endif

repeat while (Switch tab?) is (yes) not (no)


stop
@enduml
```