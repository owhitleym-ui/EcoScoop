```plantuml
@startuml
skin rose
skinparam linetype ortho

interface view.UI {
  + setListener(listener : Listener)
  + runMainMenu()
  + runArticleTab()
  + runDisplayArticle(article : model.Article)
  + runChooseArticle()
  + runDisplayArticleList(articleList : List<model.Article>)
  + runSearchArticles()
  + runSearchInput()
  + runDisplaySearchResults(results : List<model.Article>)
  + runSortOptions(results : List<model.Article>)
}

class view.CmdLineUI {
  - iscanner : Scanner
  - ostream : PrintStream
  - listener : Listener
  + view.CmdLineUI()
  + clearConsole()
}

interface UIListener {
  + onViewArticleTab()
  + onGetArticle(id : int)
  + onDisplayArticle(article : model.Article)
  + onChooseArticle()
  + onDisplayArticleList()
  + onSearchArticles()
  + onSearchQuery(query : String, type : String) : List<model.Article>
  + onSortResults(results : List<model.Article>, criteria : String) : List<model.Article>
}

class controller.Controller {
  - ui : view.UI
  - retriever : model.ArticleRetriever
  - articleList : List<model.Article>
  - curArticle : model.Article
  + controller.Controller(ui : view.UI)
  + main(args : String[])
}

class model.ArticleRetriever {
  + databaseMap : Map<Integer, model.Article>
  + articleList : List<model.Article>
  - folderManager : model.FolderManager
  + model.ArticleRetriever()
  + getArticle(id : int) : model.Article
  + searchArticles(query : String, type : String) : List<model.Article>
  + sortArticles(articles : List<model.Article>, criteria : String) : List<model.Article>
  + createFolder(name : String) : model.Folder
  + deleteFolder(name : String) : boolean
  + getFolder(name : String) : model.Folder
  + saveToFolder(articleId : int, folderName : String)
  + getFolders() : List<model.Folder>
}

class model.ArticleDatabase {
  + database : Map<Integer, model.Article>
  + articles : List<model.Article>
  ~ app : model.ArticleParser
  + model.ArticleDatabase()
  + getDatabase() : Map<Integer, model.Article>
  + saveArticles(articles : List<model.Article>)
}

class model.FeedFetcher {
  + fetchAll(feeds : Map<String, String>) : List<model.Article>
}

class model.ArticleParser {
  - articleList : List<model.Article>
  - tagList : List<String>
  - authorList : List<String>
  - content : List<String>
  + model.ArticleParser()
  + parse(args : String[], content : String, fileWebsite : String)
  + loadArticles() : List<model.Article>
}

class model.Article {
  - id : int
  - title : String
  - description : String
  - authors : List<model.Author>
  - source : model.Source
  - tagList : List<model.Tag>
  - content : String
  - publishDate : String
  + model.Article()
  + model.Article(id, title, description, authors, tagList, source, content)
  + printArticle() : String
  + getSummary() : String
  + getContent() : String
  + addLike()
  + addDislike()
  + matchesSearch(query : String) : boolean
  + getId() : int
  + getTitle() : String
  + getDescription() : String
  + getAuthors() : List<model.Author>
  + getTagList() : List<model.Tag>
  + getSource() : model.Source
  + getLikes() : int
  + getDislikes() : int
}

class model.Author {
  - name : String
  + model.Author(name : String)
  + getName() : String
  + toString() : String
}

class model.Tag {
  - name : String
  + model.Tag(name : String)
  + getName() : String
  + toString() : String
}

class model.Source {
  - websiteName : String
  - url : String
  - publishDate : String
  + model.Source(name : String, url : String, date : String)
  + getWebsiteName() : String
  + getUrl() : String
  + getPublishDate() : String
  + toString() : String
}

class model.FolderManager {
  - folders : List<model.Folder>
  - retriever : model.ArticleRetriever
  + model.FolderManager(retriever : model.ArticleRetriever)
  + createFolder(name : String) : model.Folder
  + deleteFolder(name : String) : boolean
  + getFolder(name : String) : model.Folder
  + saveToFolder(articleId : int, folderName : String)
  + getFolders() : List<model.Folder>
}

class model.Folder {
  - name : String
  - articleIds : List<Integer>
  - retriever : model.ArticleRetriever
  + model.Folder(name : String, retriever : model.ArticleRetriever)
  + getFolderName() : String
  + rename(newName : String)
  + addArticle(id : int)
  + removeArticle(id : int)
  + open() : List<model.Article>
}

class Main {
  + main(args : String[])
}

' Front-End Associations
view.CmdLineUI ..|> view.UI : implements
controller.Controller ..|> UIListener : implements
controller.Controller "1" --> "(1) ui" view.UI : delegates to
UIListener "0..1" -- "1" view.CmdLineUI

' Back-End Associations
controller.Controller "1" --> "1" model.ArticleRetriever : queries
model.ArticleRetriever "1" --> "1" model.ArticleDatabase : gets articles
model.ArticleDatabase "1" --> "1" model.FeedFetcher : uses
model.FeedFetcher "1" ..> "0..*" model.ArticleParser : creates per feed
model.ArticleParser ..> model.Article : creates
model.ArticleParser ..> model.Author : creates
model.ArticleParser ..> model.Tag : creates
model.ArticleParser ..> model.Source : creates

' Domain Associations
model.Article "1" *-- "1" model.Source : contains
model.Article "1" *-- "0..*" model.Author : written by
model.Article "1" *-- "0..*" model.Tag : categorized by

' model.Folder Associations
model.ArticleRetriever "1" *-- "1" model.FolderManager : owns
model.FolderManager "1" *-- "0..*" model.Folder : manages
model.Folder "0..*" --> "1" model.ArticleRetriever : looks up articles

' Test Harness
Main ..> model.ArticleRetriever : uses

@enduml
```
class-diagram.md
Displaying class-diagram.md.