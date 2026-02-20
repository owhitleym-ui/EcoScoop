# Save Article

## 1. Primary actor and goals

__User__: Ease of access storing articles for later reading or saving them in preferred folders. 

## 2. Other stakeholders and their goals

* __Author__: Wants to know how many saves their article has gained.


## 3. Preconditions
* User is authenticated
* User switches to Article Section
* User accesses Article
* User has clicked Save Article Button

## 4. Postconditions
* Stores Article into a Saved Folder
* Author is able to view how much saves

## 5. Workflow

```plantuml
@startuml

skin rose

title Save Article (Casual)

'define the lanes
|#application|User|
|#implementation|System|

|System|
start
:Send Saved Folders to User;

|User|
:Select which Folder to save Article in;
|System|
:Send Article + ID to save;

|System|
if (Validate ID) then (yes)
:Save Article + ID to user's preferred location;
:Update amount of saves on Article;
stop

else (no)
:Do not save article;
:Display Unable to Save Article;

stop
@enduml
```