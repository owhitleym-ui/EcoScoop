```plantuml
@startuml
skin rose
skinparam linetype ortho

interface view.UI {
  + setListener(listener : Listener)
  + runMainMenu()
  + runArticleTab()
  + runDisplayArticle(article : Article)
  + runChooseArticle()
  + runDisplayArticleList(articleList : List<Article>)
  + runSearchArticles()
  + runSearchInput()
  + runDisplaySearchResults(results : List<Article>)
  + runSortOptions(results : List<Article>)
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
  + getArticleCount() : int
  + onDisplayArticle(article : Article)
  + onChooseArticle()
  + onDisplayArticleList()
  + onSearchArticles()
  + onSearchQuery(query : String, type : String) : List<Article>
  + onSortResults(results : List<Article>, criteria : String) : List<Article>
  + onSaveToFolder(articleId : int, folderName : String)
  + onLikeArticle(id : int)
  + onDislikeArticle(id : int)
  + onCommentArticle(id : int, comment : String)
}

class controller.Controller {
  - ui : UI
  - retriever : ArticleRetriever
  - articleList : List<Article>
  - curArticle : Article
  + controller.Controller(ui : view.UI)
  + main(args : String[])
}

class model.ArticleRetriever {
  + databaseMap : Map<Integer, Article>
  + articleList : List<Article>
  - folderManager : FolderManager
  + ArticleRetriever()
  + getArticle(id : int) : Article
  + searchArticles(query : String, type : String) : List<Article>
  + sortArticles(articles : List<Article>, criteria : String) : List<Article>
  + createFolder(name : String) : Folder
  + deleteFolder(name : String) : boolean
  + getFolder(name : String) : Folder
  + saveToFolder(articleId : int, folderName : String)
  + getFolders() : List<Folder>
}

class model.ArticleDatabase {
  + database : Map<Integer, Article>
  + articles : List<Article>
  ~ app : ArticleParser
  + ArticleDatabase()
  + getDatabase() : Map<Integer, Article>
  + saveArticles(articles : List<Article>)
}

class model.FeedFetcher {
  + fetchAll(feeds : Map<String, String>) : List<Article>
}

class model.ArticleParser {
  - articleList : List<Article>
  - tagList : List<String>
  - authorList : List<String>
  - content : List<String>
  + ArticleParser()
  + parse(args : String[], content : String, fileWebsite : String)
  + loadArticles() : List<Article>
}

class model.Article {
  - id : int
  - title : String
  - description : String
  - authors : List<Author>
  - source : Source
  - tagList : List<Tag>
  - content : String
  - publishDate : String
  - likes : int
  - dislikes : int
  - comments : List<String>
  + Article()
  + Article(id, title, description, authors, tagList, source, content)
  + printArticle() : String
  + getSummary() : String
  + getContent() : String
  + addLike()
  + addDislike()
  + addComment(comment : String)
  + getComments() : List<String>
  + matchesSearch(query : String) : boolean
  + getId() : int
  + getTitle() : String
  + getDescription() : String
  + getAuthors() : List<Author>
  + getTagList() : List<.Tag>
  + getSource() : Source
  + getLikes() : int
  + getDislikes() : int
}

class model.Author {
  - name : String
  + Author(name : String)
  + getName() : String
  + toString() : String
}

class model.Tag {
  - name : String
  + Tag(name : String)
  + getName() : String
  + toString() : String
}

class model.Source {
  - websiteName : String
  - url : String
  - publishDate : String
  + Source(name : String, url : String, date : String)
  + getWebsiteName() : String
  + getUrl() : String
  + getPublishDate() : String
  + toString() : String
}

class model.FolderManager {
  - folders : List<Folder>
  - retriever : ArticleRetriever
  + FolderManager(retriever : ArticleRetriever)
  + createFolder(name : String) : Folder
  + deleteFolder(name : String) : boolean
  + getFolder(name : String) : Folder
  + saveToFolder(articleId : int, folderName : String)
  + getFolders() : List<Folder>
}

class model.Folder {
  - name : String
  - articleIds : List<Integer>
  - retriever : ArticleRetriever
  + Folder(name : String, retriever : ArticleRetriever)
  + getFolderName() : String
  + rename(newName : String)
  + addArticle(id : int)
  + removeArticle(id : int)
  + open() : List<Article>
}
' Front-End Associations
CmdLineUI ..|> UI : implements
Controller ..|> UIListener : implements
Controller "1" --> "(1) ui" UI : delegates to
UIListener "0..1" -- "1" CmdLineUI

' Back-End Associations
controller.Controller "1" --> "1" ArticleRetriever : queries
ArticleRetriever "1" --> "1" ArticleDatabase : gets articles
ArticleDatabase "1" --> "1" FeedFetcher : uses
FeedFetcher "1" ..> "0..*" ArticleParser : creates per feed
ArticleParser ..> Article : creates
ArticleParser ..> Author : creates
ArticleParser ..> Tag : creates
ArticleParser ..> Source : creates

' Domain Associations
Article "1" *-- "1" Source : contains
Article "1" *-- "0..*" Author : written by
Article "1" *-- "0..*" Tag : categorized by

' Folder Associations
ArticleRetriever "1" *-- "1" FolderManager : owns
FolderManager "1" *-- "0..*" Folder : manages
Folder "0..*" --> "1" ArticleRetriever : looks up articles

@enduml
```