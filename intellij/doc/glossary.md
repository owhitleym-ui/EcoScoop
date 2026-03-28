
# EcoScoop - Glossary 


| Term            | Definition                                                                                                 | Format              | Aliases                         |
|-----------------|------------------------------------------------------------------------------------------------------------|---------------------|---------------------------------|
| Article         | A piece of writing included with others in newspapers, magazines, or other print or online publications    | html.file           | Text, Source, Writing           |
| Save            | Storing previous articles into a memory system for later access                                            | Saved/Not Saved     | Store                           |
| ID              | Sequence of numbers used to categorize and distinguish articles                                            | sequence of numbers | N/A                             |
| Gamification    | The use of game-like elements (points, badges, leaderboards) to increase user engagement                   | N/A                 | Gamified                        |
| ESS             | Environmental Statistics System - used to collect information on the current environment (local or global) | N/A                 | Environmental Statistics System |
| RSS Feed        | Really Simple Syndication — a web format used by news sites to publish frequently updated content that apps can fetch automatically | URL (XML) | Feed |
| Tag             | A topic label attached to an article used to categorize it (e.g. "Energy", "Politics")                    | String              | Category                        |
| Author          | The person or organization credited with writing an article                                                | String              | Creator, Writer                 |
| Source          | The website an article was fetched from, including its name, URL, and publish date                         | Object              | Publisher, Origin               |
| Folder          | A user-created named collection for saving articles to read later                                          | Named list          | Collection, Bookmark folder     |
| Reaction        | A user response to an article — either a like, dislike, or comment                                        | Like / Dislike / Comment | Feedback               |
| Like            | A positive reaction a user gives to an article, incrementing its like count                                | Integer count       | Upvote                          |
| Dislike         | A negative reaction a user gives to an article, incrementing its dislike count                             | Integer count       | Downvote                        |
| Comment         | A text response a user leaves on an article, stored in that article's comment list                         | String              | Review, Note                    |
| MVC             | Model-View-Controller — the architecture pattern used to separate data (model), display (view), and logic (controller) | Design pattern | N/A |
| Controller      | The class that sits between the UI and the data layer, coordinating user actions with model updates        | Java class          | N/A                             |
| RSS Parser      | The component that reads raw RSS/XML feed data and converts it into Article objects                        | Java class          | ArticleParser                   |
| Keyword Search  | A search that looks for matching text across an article's title, description, and body content             | String query        | Full-text search                |