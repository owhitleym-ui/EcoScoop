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
switch (Open Eco Dashboard)
    case (Click)
    :Open fossil fuel data;
    case (Click)
    :Open energy consumption data;
    case (Click)
    :Check air emissions;
    case(Click)
    :Open waste data;
    case (Click)
    :Open water data;
endswitch
|System|
:Extend diagram view;
stop
@enduml
```