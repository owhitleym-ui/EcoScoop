# React model.Article

## 1. Primary actor and goals

__User__: Ease of access giving feedback on article content from personal impressions and thoughts. Able to Upvote or Downvote articles based off enjoyment or other opinions. Ease of access leaving comments to any articles they have strong opinions about.


## 2. Other stakeholders and their goals

* __Websites__: Wants to know how many reactions the article has gotten for later use.
* __Author__: Wants feedback from users on their article, probably positive. Prefers likes to dislikes. Wants user to check out other articles they have written or to subscribe to their profile.

## 3. Preconditions
* User is authenticated
* User is in articles tab and clicks an article.
* User has opened and read through an article.

## 4. Postconditions
* Liked article is saved to section in profile.
* Preferences are saved, so user receives more liked content and less disliked content.
* Comment is saved to user history.
* Stats are updated in user profile.
* Displays reaction (comment or like/dislike)

## 5. Workflow

```plantuml
@startuml

skin rose

title React model.Article(Casual)

'define the lanes
|#application|User|
|#implementation|System|


|User|
start
repeat
:Give feedback on article;
|System|
switch (Handle Feedback)
    
    case ( Open Comment Section?)
        if(Commented?) then (yes)
            |User|
            :Edit/Delete Comment;
            |System|
            :Updates or deletes Comment;
        (no)elseif (Make Comment?) then (yes)
            |System|
            :Add comment to profile history;
        (no)elseif (Upvote or Downvote Comment?)
            |System|
            :Update comment stats;
        endif
    case (      Like model.Article?)
        |System|
        :Execute __Save Article__;
        :Show more related content;
    case ( Dislike model.Article?)
        :Show less content like this;
    case ( Save model.Author?) 
        |System|
        :Follow author;
        :Show author's content in articles tab;
endswitch


|User|
repeat while (Finished reaction?) is (no) not (yes)
:Finish reaction;
stop
@enduml
```
## 6. Sequence Diagram
```plantuml
@startuml
skin rose
hide footbox
title React model.Article (Sequence)

actor User
participant ": view.CmdLineUI" as view.UI
participant ": controller.Controller" as controller.Controller
participant "a : model.Article" as model.Article

note over view.UI : Shown automatically after\nthe user exits an article

view.UI -> User : display react prompt\n(skip / like / dislike / comment)

alt like
    User -> view.UI : enter 1
    view.UI -> controller.Controller : onLikeArticle(articleId)
    controller.Controller -> model.Article : addLike()
    view.UI --> User : "Liked! (N likes)"

else dislike
    User -> view.UI : enter 2
    view.UI -> controller.Controller : onDislikeArticle(articleId)
    controller.Controller -> model.Article : addDislike()
    view.UI --> User : "Disliked! (N dislikes)"

else comment
    User -> view.UI : enter 3
    view.UI -> User : prompt for comment text
    User -> view.UI : enter comment
    view.UI -> controller.Controller : onCommentArticle(articleId, comment)
    controller.Controller -> model.Article : addComment(comment)
    view.UI --> User : "Comment added."

else skip
    User -> view.UI : enter 0
    view.UI --> User : return to article list

end

@enduml
```