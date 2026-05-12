# React Article

## 1. Primary actor and goals

__User__: Wants to like, dislike, or comment on an article they have read. Likes and dislikes are mutually exclusive toggles. Comments are stored per-session and removable from the profile screen.

## 2. Other stakeholders and their goals

* __Authors__: Benefit from knowing how many users reacted positively.

## 3. Preconditions
* User has opened an article in the Display Article screen.

## 4. Postconditions
* Like/dislike count is updated (mutual exclusion enforced).
* Comment is appended to the article and to the user's comment history.
* User can remove their own comments from the Profile screen.

## 5. Workflow
```plantuml
@startuml
skin rose

title React to Article (Casual)

|#application|User|
|#implementation|System|

|User|
start
:Open article;

switch (Choose reaction)
case (Like)
  |System|
  if (Already disliked?) then (yes)
    :Remove dislike first;
  endif
  :Increment like count;
  :Update button state;
case (Dislike)
  |System|
  if (Already liked?) then (yes)
    :Remove like first;
  endif
  :Increment dislike count;
  :Update button state;
case (Comment)
  |User|
  :Type comment text;
  |System|
  :Append comment to article;
  :Append comment to User history;
case (Remove comment)
  |User|
  :Navigate to Profile → Comments;
  :Tap ✕ on comment row;
  |System|
  :Remove comment at index from User;
  :Refresh comment list;
endswitch

|User|
:Continue reading or navigate back;
stop
@enduml
```

## 6. Sequence Diagrams

```plantuml
@startuml
skin rose
hide footbox
title Like Article

actor User as user
participant "fragment : DisplayArticleFragment" as UI
participant "activity : ControllerActivity" as controller
participant "ar : ArticleRetriever" as AR
participant "art : Article" as article

user -> UI : taps Like button
UI -> controller : onLikeArticle(id)
controller -> AR : getArticle(id)
AR --> controller : art
controller -> article : addLike()
controller -> UI : update like count display
UI --> user : Like button highlighted, count incremented

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Dislike Article

actor User as user
participant "fragment : DisplayArticleFragment" as UI
participant "activity : ControllerActivity" as controller
participant "ar : ArticleRetriever" as AR
participant "art : Article" as article

user -> UI : taps Dislike button
UI -> controller : onDislikeArticle(id)
controller -> AR : getArticle(id)
AR --> controller : art
controller -> article : addDislike()
controller -> UI : update dislike count display
UI --> user : Dislike button highlighted, count incremented

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Comment on Article

actor User as user
participant "fragment : DisplayArticleFragment" as UI
participant "activity : ControllerActivity" as controller
participant "ar : ArticleRetriever" as AR
participant "art : Article" as article
participant "u : User" as userModel

user -> UI : taps Add Comment, enters text, confirms
UI -> controller : onCommentArticle(id, comment)
controller -> AR : getArticle(id)
AR --> controller : art
controller -> article : addComment(comment)
controller -> userModel : addComment(comment)
UI --> user : comment appears in list below article

@enduml
```

```plantuml
@startuml
skin rose
hide footbox
title Remove Comment from Profile

actor User as user
participant "fragment : ProfileFragment" as UI
participant "activity : ControllerActivity" as controller
participant "u : User" as userModel

user -> UI : navigates to Profile → Comments
UI -> controller : onGetUserComments()
controller -> userModel : getComments()
userModel --> controller : List<String>
controller --> UI : comment list
UI --> user : each comment shown with ✕ button

user -> UI : taps ✕ on a comment
UI -> UI : show confirmation dialog
user -> UI : confirms Remove
UI -> controller : onRemoveComment(index)
controller -> userModel : removeComment(index)
UI -> UI : loadComments()
UI --> user : updated comment list

@enduml
```
