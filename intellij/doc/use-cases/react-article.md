# React Article

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

title React Article(Casual)

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
    case (      Like Article?)
        |System|
        :Execute __Save Article__;
        :Show more related content;
    case ( Dislike Article?)
        :Show less content like this;
    case ( Save Author?) 
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
title React Article (Sequence)

actor User
participant ": System UI" as UI
participant ": Controller" as Controller
participant ": RecommendationEngine" as Rec


UI -> User : display reaction 
    alt new comment
        User -> UI : submit comment
        UI -> Controller : addComment(articleId, input)
        Controller --> UI : display comment\nadd to user history

    else edit or delete existing comment
        User -> UI : edit / delete comment
        UI -> Controller : editComment(commentId, input)\nor deleteComment(commentId)
        Controller --> UI : update comment display

    else upvote or downvote comment
        User -> UI : react to comment
        UI -> Controller : reactToComment(commentId, type)
        Controller --> UI : update comment stats


else like article
    User -> UI : click like
    ref over UI, Controller
        Save Article
    end ref
    Controller -> Rec : moreLikeThis(articleId)
    Rec --> UI : update recommendations

else dislike article
    User -> UI : click dislike
    UI -> Controller : dislikeArticle(articleId)
    Controller -> Rec : lessLikeThis(articleId)
    Rec --> UI : update recommendations

end

@enduml
```