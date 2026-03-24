```plantuml
@startuml
skin rose
skinparam linetype ortho

class SearchController {
  + submitSearch(query : String, type : String) : List<Article>
  + sortArticles(criteria : String) : List<Article>
}

class ArticleController {
  + getArticle(id : int) : Article
  + saveArticle(id : int) : void
  + calculatePoints() : double
  + recommendArticles() : List<Article>
  + displayArticle(id : int, content : String) : void
}

class ArticleRetriever {
  + getArticles() : List<Article>
  + searchArticles() : List<Article>
  + fetchHistory() : List<Article>
  + executeAccessArticle(id : int) : Article
}

class ArticleDatabase {
  - articleList : Set<Article>
  - articleIds : Set<Integer>
  + save(articles : List<Article>) : void
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

class User {
  - username : String
  - points : double
  - savedArticles : List<List<Article>>
}

class Folder {
  - name : String
  - contents : List<Article>
  + open() : void
  + addArticle(article : Article) : void
  + removeArticle(id : int) : void
  + edit(newName : String) : void
  + delete() : void
}

enum ArticleTag {
  - name : String
}

class ArticleInteraction {
  - reactionType : String
  - saved : bool
  - comment : String
}

class Author {
  - name : String
}

class Source {
  - name : String
  - url : String
}

SearchController --> ArticleController : delegates to
SearchController --> ArticleRetriever : uses
ArticleController --> ArticleRetriever : uses
ArticleRetriever --> ArticleDatabase : queries
ArticleRetriever --> Article : creates
ArticleController --> Article : manages
ArticleController --> User : updates
ArticleController --> Folder : manages
SearchController --> Article : returns
Author "1" --> "0..*" Article : writes
Source "1" --> "0..*" Article : publishes
Article "*" --> "*" ArticleTag : tagged with
User "1" --> "0..*" ArticleInteraction : has
User "1" --> "0..*" Folder : owns
Article "1" --> "0..*" ArticleInteraction : involved in
User "*" --> "*" ArticleTag : prefers
Folder "0..*" --> "*" Article : contains

@enduml
```
