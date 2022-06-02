# Search Analytics Use Case

## Topology

1. Cloud
2. Country
3. City

## Functions

| Name                              | Type              | Where     | Callers                                                           |
|-----------------------------------|-------------------|-----------|-------------------------------------------------------------------|
| `search-analytics-data-receivers` | Offloadable       | City      | Called by users when they perform a search                        |
| `search-analytics-store-data`     | ReceivePropagate  | Country   | Called by `search-analytics-data-receivers` through `propagate()` |
| `search-analytics-performer`      |                   | Country   | Called by users to look at the search analytics                   |

* `search-analytics-data-receivers` collect the searches from users and propagate them to `search-analytics-store-data`
* `search-analytics-store-data` receives the searches and stores them in the latest local database
* `search-analytics-performer` fetch searches in all the local databases

> Optimization: instead of propagating every single search, we could store them in `search-analytics-data-receivers`'s local database and send them once a minute leveraging cron-connector

## Database

Users' searches are stored with a 1 minute granularity. We store 4 hours of searches. We associate one local database to every minute, hence every minute a new session is created and the oldest local database is deleted (it can live up to 4 hours and 1 minute). The system will reach the total number of sessions (60 minutes * 4 hours = 240 local databases) after 4 hours it started.
The name of the sessions are the current time in minutes.
