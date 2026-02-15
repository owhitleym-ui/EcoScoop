
# NextGen Point Of Sale system - Vision document 

## 1. Introduction

We envision a robust News Hub application, EcoScoop, with the ability to support multiple news articles and user-based interaction, supporting a gameified feel to support usage.

## 2. Business case
Our News Hub application addresses customer needs that other products do not:

1. It supports user-oriented preferences and feed.
2. It provides non-biased eco-sustainable news articles and filters through false information.
3. It integrates game aspects to create a fun interactive environment.

## 3. Key functionality
- Provides Articles and News Feeds focusing on eco-sustainability.
- Multiple interactable games and features (Leaderboard, Points, Games)
- Real time crawling and article updating using third party servers
- Rating System allowing for more interaction and what is trending

## 4. Stakeholder goals summary
- **User**: obtain relevant articles on environment, topical news, readable format, interact with articles
- **Author**: write articles, develop news feeds,
- **Websites**: provide articles, want credit and attribution


## Use case diagram
```plantuml
@startuml
skin rose

'human actors
actor "User" as user

'system actors
actor "Point System" <<system>> as pointSystem
actor "History System" <<system>> as historySystem
actor "Internet System" <<system>> as internetSystem
actor "Learning System" <<system>> as learningSystem

' list all use cases in package
package EcoScoop{
    usecase "Open EcoScoop" as openEcoscoop
    usecase "Open Articles" as openArticles
    usecase "Access Article" as accessArticle
    usecase "Search for Article" as searchArticle
    usecase "React to Article" as reactArticle
    usecase "Save Article" as saveArticle
    usecase "Open Profile" as openProfile
    usecase "Configure Settings" as changeSettings
    usecase "Access History" as accessHistory
    usecase "Open Dashboard" as openDashboard
    usecase "Access Eco Stats" as accessEcoStats
    usecase "Access Fun Stats" as accessFunStats
}

'list associations
user --> openEcoscoop

openEcoscoop <|-down- openArticles : <<extends>>
openEcoscoop <|-down- openProfile : <<extends>>
openEcoscoop <|-down- openDashboard : <<extends>>

openArticles <|-down- accessArticle : <<extends>>
openArticles <|-down- searchArticle : <<extends>>

accessArticle <|-right- searchArticle : <<extends>>
accessArticle <|-down- reactArticle : <<extends>>
accessArticle <|-down- saveArticle : <<extends>>

accessArticle --> pointSystem
accessArticle --> internetSystem
accessArticle --> learningSystem
reactArticle --> pointSystem
reactArticle --> historySystem
saveArticle --> historySystem

openProfile <|-down- accessHistory : <<extends>>
openProfile <|-right- changeSettings : <<includes>>

accessHistory --> historySystem 

openDashboard <|-down- accessEcoStats : <<extends>>
openDashboard <|-down- accessFunStats : <<extends>>

@enduml
```

