
# EcoScoop - Non-Functional Specification

## Usability
- Text should be easily able to change with comfort for different preferences (i.e., Light Mode, Dark Mode, text font, etc.)
- Colors associated with common forms of color blindness should be avoided.
- Gamefied options are easy to access and simple to read
- Navigation should be clear and intuitive, with minimal steps to find articles or features.
- The app should provide clear feedback when users complete actions (earning points, saving articles, etc.).

## Reliability - recoverability
- If external systems fail, the app should continue functioning using locally stored data when possible.
- The last successful refresh should be saved and used as a temporary fallback.
- If article retrieval fails, article content should be stored locally and automatically re-synced when connectivity is restored.
- The system should retry failed external requests at defined intervals.

## Performance
- Users should be able to access and load articles within 3 seconds under normal network conditions.
- Articles should refresh automatically every 15 minutes.
- The app should remain responsive during background updates.

## Supportability
- The application should support internationalization (text translation, units, number formatting, and date/time formatting).

## Implementation
- Software must run on Android devices. 
- Software must be written using Java.


## External interfaces
- Website articles must be accessed from a 3rd-party database
- The system should securely handle communication with external data sources. 
- External API failures should not crash the application.