# klibqonto

A client for the [Qonto API](https://api-doc.qonto.eu), for Kotlin and Java.

Several flavors of the client are available to match your needs:
- [Coroutines (`suspend`) based](https://github.com/BoD/klibqonto/blob/master/library/src/main/kotlin/org/jraf/klibqonto/client/QontoClient.kt): the default client for Kotlin projects
- [`Flow` based](https://github.com/BoD/klibqonto/blob/master/library/src/main/kotlin/org/jraf/klibqonto/client/flow/FlowQontoClient.kt): useful if you like "reactive style" programming
- [`Future` based](https://github.com/BoD/klibqonto/blob/master/library/src/main/kotlin/org/jraf/klibqonto/client/future/FutureQontoClient.kt): preferred for Java projects
- [Blocking](https://github.com/BoD/klibqonto/blob/master/library/src/main/kotlin/org/jraf/klibqonto/client/blocking/BlockingQontoClient.kt): useful for Java projects, or if you have your own async mechanism

## Usage
### 1/ Add the dependencies to your project
The artifact is hosted on JCenter.
```groovy
repositories {
    /* ... */
    jcenter()
}
```
```groovy
dependencies {
    /* ... */
    implementation 'org.jraf:klibqonto:1.0.0'
}
```

### 2/ Use the client

The easiest way to see how to use it is to look at the samples:
- [default (Kotlin)](sample/src/main/kotlin/org/jraf/klibqonto/sample/Sample.kt)
- [Flow (Kotlin)](sample/src/main/kotlin/org/jraf/klibqonto/sample/FlowSample.kt)
- [Future (Java)](sample/src/main/java/org/jraf/klibqonto/sample/FutureSample.java)
- [Blocking (Java)](sample/src/main/java/org/jraf/klibqonto/sample/BlockingSample.java)

#### Get your login and secret key
You will find your **login** and **secret key** in the Qonto web application under Settings, in the API tab.

#### Instanciate a `QontoClient`

```kotlin
val qontoClient = QontoClient.newInstance(
    ClientConfiguration(
        Authentication(
            LOGIN,
            SECRET_KEY
        )
    )
)
```
To get other flavors of the client:
- Flow: `val flowClient = qontoClient.asFlowQontoClient()`
- Future: `FutureQontoClient futureClient = FutureQontoClientUtils.asFutureQontoClient(qontoClient)`
- Blocking: `BlockingQontoClient blockingClient = BlockingQontoClientUtils.asBlockingQontoClient(qontoClient)`

#### Use the `QontoClient`
The client gives access to several API "areas":
- `organizations`
- `transactions`
- `memberships`
- `labels`
- `attachments`

Each area exposes related APIs, for instance: `qontoClient.transactions.getTransactionList`.

#### Pagination
The APIs that are paginated all follow the same principle:
- take a [`Pagination`](https://github.com/BoD/klibqonto/blob/master/library/src/main/kotlin/org/jraf/klibqonto/model/pagination/Pagination.kt) object as a parameter, which defines the page to retrieve, as well as the number of items per page
- return a [`Page<T>`](https://github.com/BoD/klibqonto/blob/master/library/src/main/kotlin/org/jraf/klibqonto/model/pagination/Page.kt) with the result list but also a reference to the next and previous `Pagination` objects (handy when retrieving several pages).

#### Logging
To log HTTP requests/response, pass a [`HttpConfiguration`](https://github.com/BoD/klibqonto/blob/master/library/src/main/kotlin/org/jraf/klibqonto/client/HttpConfiguration.kt) to `QontoClient.newInstance()`.

Other logs are available via `slf4j` - one way to enable them is `System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")`

## Author and License
*Note: this project is not officially related to or endorsed by Qonto or Olinda SAS.*

```
Copyright (C) 2019-present Benoit 'BoD' Lubek (BoD@JRAF.org)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
