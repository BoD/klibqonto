# klibqonto

A [Qonto API](https://api-doc.qonto.com) client library for Kotlin, Java and more.

This library is written in [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html)
so _in theory_ it can be used from the JVM, Android, and native iOS, Linux, MacOS, Windows and more.
In practice this library has been tested and has samples for the JVM
(Kotlin and Java), Android (Kotlin), MacOS (Kotlin), and iOS (Swift).

Several flavors of the client are available to match your needs:
- [Coroutines (`suspend`) based](https://github.com/BoD/klibqonto/blob/master/library/src/commonMain/kotlin/org/jraf/klibqonto/client/QontoClient.kt): the default client for Kotlin projects
- [Blocking](https://github.com/BoD/klibqonto/blob/master/library/src/commonMain/kotlin/org/jraf/klibqonto/client/blocking/BlockingQontoClient.kt): useful for Java projects, or if you have your own async mechanism
- [Callback based](https://github.com/BoD/klibqonto/blob/master/library/src/commonMain/kotlin/org/jraf/klibqonto/client/callback/CallbackQontoClient.kt): useful for Java and Swift projects
- [`Future` based (JVM only)](https://github.com/BoD/klibqonto/blob/master/library/src/jvmMain/kotlin/org/jraf/klibqonto/client/future/FutureQontoClient.kt): useful for Java projects

## Usage

### 1/ Add the dependencies to your project

#### Gradle based projects

The artifacts are hosted on the Maven Central repository.

Note: prior to v2.3.0, the artifacts used to be hosted on JCenter.

```groovy
repositories {
    /* ... */
    mavenCentral()
}
```

```groovy
dependencies {
    /* ... */
    implementation 'org.jraf:klibqonto:2.5.0'
}
```

#### OSX based projects
**TODO**

### 2/ Use the client

The easiest way to see how to use it is to look at the samples:

- [Coroutines (Kotlin)](samples/sample-jvm/src/main/kotlin/org/jraf/klibqonto/sample/Sample.kt)
- [Blocking (Java)](samples/sample-jvm/src/main/java/org/jraf/klibqonto/sample/BlockingSample.java)
- [Future (Java)](samples/sample-jvm/src/main/java/org/jraf/klibqonto/sample/FutureSample.java)
- [Callback (Kotlin)](samples/sample-jvm/src/main/kotlin/org/jraf/klibqonto/sample/CallbackSample.kt)
- Callback (Swift) **TODO**

#### Authentication by login and secret key

You will find your **login** and **secret key** in the Qonto web application under Settings, in the Integrations (API)
tab.

#### Authentication by OAuth

To use OAuth, you must register your application by reaching out to Qonto as
documented [here](https://api-doc.qonto.com/2.0/welcome/register-your-application).

OAuth usage is demonstrated in the [sample](samples/sample-jvm/src/main/kotlin/org/jraf/klibqonto/sample/Sample.kt).

#### Instantiate a `QontoClient`

```kotlin
val qontoClient = QontoClient.newInstance(
    ClientConfiguration(
        LoginSecretKeyAuthentication(
            LOGIN,
            SECRET_KEY
        )
        // Or use OAuthAuthentication for OAuth
    )
)
```
To get other flavors of the client:
- Blocking: `BlockingQontoClient blockingClient = BlockingQontoClientUtils.asBlockingQontoClient(qontoClient)`
- Callback: `CallbackQontoClient callbackClient = CallbackQontoClientUtils.asCallbackQontoClient(qontoClient)`
- Future: `FutureQontoClient futureClient = FutureQontoClientUtils.asFutureQontoClient(qontoClient)`

#### Use the `QontoClient`
The client gives access to several API "areas":

- `oAuth`
- `organizations`
- `transactions`
- `memberships`
- `labels`
- `attachments`

Each area exposes related APIs, for instance: `qontoClient.transactions.getTransactionList`.

#### Pagination
The APIs that are paginated all follow the same principle:
- take a [`Pagination`](https://github.com/BoD/klibqonto/blob/master/library/src/commonMain/kotlin/org/jraf/klibqonto/model/pagination/Pagination.kt) object as a parameter, which defines the page to retrieve, as well as the number of items per page
- return a [`Page<T>`](https://github.com/BoD/klibqonto/blob/master/library/src/commonMain/kotlin/org/jraf/klibqonto/model/pagination/Page.kt) with the result list but also a reference to the next and previous `Pagination` objects (handy when retrieving several pages).

#### Logging
To log HTTP requests/response, pass a [`HttpConfiguration`](https://github.com/BoD/klibqonto/blob/master/library/src/commonMain/kotlin/org/jraf/klibqonto/client/HttpConfiguration.kt) to `QontoClient.newInstance()`.

Several levels are available: `NONE`, `INFO`, `HEADERS`, `BODY` and `ALL`

#### Proxy
A proxy can be configured by passing a [`HttpConfiguration`](https://github.com/BoD/klibqonto/blob/master/library/src/commonMain/kotlin/org/jraf/klibqonto/client/HttpConfiguration.kt) to `QontoClient.newInstance()`.

On Android, the proxy set in the system settings is automatically used.

## Javascript support
In theory Kotlin Multiplatform projects can also target Javascript
but as of today the author couldn't understand how to make
that work.  Please [contact the author](mailto:BoD@JRAF.org) if you want to help :)

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
