# INFO7255 33256 Adv Big-Data App/Indexing SEC 02 Spring 2024 [VTL-2-OL]

## Course description
New data points are being generated at ever increasing rates. Traditional techniques based on relational databases to
ingesting, storing, indexing, and analyzing the data are no longer sufficient to deal with the volume, variety, and
velocity of new data points. The volume, variety, and velocity of new data points are creating bottlenecks at every
stage of the processing chain. This course will present Big Data architecture for building distributed software systems.
At the outer endpoint of the distributed system, there is a need to quickly validate the incoming data so as to maintain
data quality. When storing the data, write latency can never exceed the tens of milliseconds for any real world
application with a healthy user base. When indexing the data, the indexer throughput rate must be high enough to keep up
with velocity increase of the incoming data. The indexing technique must support logical operators, wildcards,
geolocation, join, and aggregate queries. Once the data is stored and indexed, we are faced with other challenges
related to near real-time predictive analytics. The issue for near real time analytics is how quickly we can take
advantage of new data points after they are stored in the system to answer a question. This requires that the duration
of the workflow required to ingest, store, index, and analyze the data be kept to a minimum. Even after all these
requirements are met, there is one additional requirement. The above system must be schema less. That is, the system
must support extensibility of its own data models and the addition of new data models without any new programming.

## Technology Stack
- Java
- Spring Boot
- Redis
- OAuth 2.0
- Elasticsearch
- RabbitMQ

## First Demo - 2/10/2024
- Rest API that can handle any structured data in Json 
  - Specify URIs, status codes, headers, data model, version
- Rest API with support for crd operations
  - Post, Get, Delete
- Rest API with support for validation
  - Json Schema describing the data model for the use case
- Controller validates incoming payloads against json schema
- The semantics with ReST API operations such as update if not changed/read if changed
  - Update not required
  - Conditional read is required
- Storage of data in key/value store
  - Must implement the use case provided

