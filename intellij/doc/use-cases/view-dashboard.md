# View Eco Dashboard

## 1. Primary actor and goals

__User__: Wants a quick, local view of current weather and long-term climate signals — temperature, precipitation, wind, UV index, heat stress, and drought — to understand how climate change is affecting their area.

## 2. Other stakeholders and their goals

* __Open-Meteo__: Provides free weather and ERA5 historical climate data via JSON API.

## 3. Preconditions

* App has launched and the Dashboard tab is selected (it is the default landing screen).
* Device has internet access.
* Location permission is granted (or a GPS timeout fallback kicks in).

## 4. Postconditions

* Current conditions, 7-day forecast, and climate anomaly card are populated.
* Tapping any card opens a detail bottom sheet.
* User can search a city or refresh GPS to change the location.

## 5. Workflow
```plantuml
@startuml
skin rose

title View Dashboard (Casual)

|#application|User|
|#implementation|System|
|#technology|Open-Meteo API|

|System|
start
:Request GPS location;

|Open-Meteo API|
:Fetch 7-day forecast\n(temp, precip, wind, UV,\nfeels-like, ET₀);
:Fetch ERA5 climate archive\n(historical daily averages);

|System|
:Store weather + climate\nin EcoRepository;
:Reverse-geocode lat/lon → city name;
:Render dashboard cards;

|User|
:View location, current temp,\n7-day strip, metric cards;

if (Tap a card?) then (yes)
  |System|
  :Show detail bottom sheet\n(stats + daily table);
  |User|
  :Read detail, dismiss sheet;
endif

if (Search city?) then (yes)
  |User|
  :Type city name, press Search;
  |System|
  :Geocode city → lat/lon;
  :Re-fetch weather for new location;
  :Update all cards;
endif

if (Tap GPS icon?) then (yes)
  |System|
  :Re-request device location;
  :Re-fetch weather;
endif

|User|
:Navigate to another tab;
stop
@enduml
```

## 6. Sequence Diagrams

```plantuml
@startuml
skin rose
hide footbox
title Load Dashboard on App Start

actor User as user
participant "activity : ControllerActivity" as controller
participant "fragment : DashboardFragment" as dash
participant "fetcher : EcoDataFetcher" as fetcher
participant "repo : EcoRepository" as repo
participant "retriever : EcoDataRetriever" as retriever

user -> controller : app launches
controller -> controller : requestFreshLocation()
note right : FusedLocationProviderClient\nwith 15 s GPS timeout fallback

controller -> controller : 15 s timeout or GPS fix received

alt GPS fix received
  controller -> controller : reverseGeocode(lat, lon) → city label
  controller -> dash : setLocationLabel("📍 CityName")
else GPS timeout
  controller -> controller : use default lat/lon (New York)
end

controller -> fetcher : fetch(lat, lon)
note right : background thread;\nOpen-Meteo forecast API
fetcher --> controller : WeatherData

controller -> fetcher : fetchClimate(lat, lon)
note right : Open-Meteo ERA5 archive
fetcher --> controller : ClimateData

controller -> repo : saveWeather(weatherData)
controller -> repo : saveClimate(climateData)

create retriever
controller -> retriever : new EcoDataRetriever(repo)
controller -> dash : onWeatherLoaded(retriever)
dash -> dash : bindHeader(), bindCards(),\nbindDailyStrip(), bindClimate()
dash --> user : dashboard rendered

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Tap Weather Card (Detail Sheet)

actor User as user
participant "fragment : DashboardFragment" as dash

user -> dash : tap card (e.g. UV Index)
dash -> dash : showDetailSheet("☀️", "UV Index", ...)
dash --> user : bottom sheet with stats + 7-day table

user -> dash : dismiss sheet
dash --> user : return to dashboard

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Search City

actor User as user
participant "fragment : DashboardFragment" as dash
participant "activity : ControllerActivity" as controller
participant "fetcher : EcoDataFetcher" as fetcher

user -> dash : types city name, presses Search
dash -> controller : onSearchLocation("Paris")
controller -> controller : geocodeAndFetch("Paris")
note right : Open-Meteo Geocoding API\n→ lat/lon for city
controller -> fetcher : fetch(lat, lon)
fetcher --> controller : WeatherData
controller -> dash : onWeatherLoaded(retriever)
controller -> dash : setLocationLabel("📍 Paris")
dash --> user : dashboard updated for new city

@enduml
```
