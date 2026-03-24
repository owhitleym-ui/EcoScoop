```plantuml
@startuml
skin rose
hide empty methods

'skinparam classAttributeIconSize 0

'classes

class User{
- profile : userprofile
- username : String = "John/Jane Doe"
}

class UserProfile {
- points : double
- savedArticles : List <List <Article>>
}

class Article {
- id : int
- title : String
- description : String
- author : String
- url : String
- content : String
- publishDate : String
}

class ArticleController{

}

class SearchController{

}

class ArticleRetriever{
}

class ArticleDatabase{
- articleList: Set<Article> [1..*]
- articleIds : Set<Integer> [1..*]
}

@enduml
```


class ArticleDescription{
description
likes
author
dislikes
}

class Article{
title
content
publishDate
url
}

class ArticleTag{
name
}

class Author{
name
}

class Source{
name
url
}

class User{
userId
points
}

class ArticleInteraction{
reactionType
saved
}

class ArticleRetriever {
+getArticles()
+searchArticles()
}

class RecommendationService {
+recommendArticles(user)
}

class UserProgressService {
+updatePoints(user)
+saveInteraction(interaction)
}


' associations
ArticleDescription "1" -- "0..*" Article : describes
Author "1" -- "0..*" Article : writes
Source "1" -- "0..*" Article : publishes

Article "*" -- "*" ArticleTag : tagged with
class ArticleDescription{
description
likes
author
dislikes
}

class Article{
title
content
publishDate
url
}

class ArticleTag{
name
}

class Author{
name
}

class Source{
name
url
}

class User{
userId
points
}

class ArticleInteraction{
reactionType
saved
}

User "1" -- "0..*" ArticleInteraction : has
Article "1" -- "0..*" ArticleInteraction : involved in

User "*" -- "*" ArticleTag : prefers

'Systems
