Twitter OAuth for Android KT
============================

This is a very basic Android application written in [Kotlin](http://kotlinlang.org) that handles Twitter OAuth.

Getting Started
---------------

1. [Install the "Kotlin" and "Kotlin Extensions for Android" plugins](http://blog.jetbrains.com/kotlin/2013/08/working-with-kotlin-in-android-studio/) for Android Studio if you don't already have them
2. [Create an app](https://apps.twitter.com/app/new) at Twitter's developer site
3. Copy the consumer key and the consumer secret key created in the above step into `~/.gradle/gradle.properties`
(creating it if necessary) as follows, replacing everything between the quotes for both lines with the respective keys:

    ```
    TwitterConsumerKey = "****** YOUR CONSUMER KEY ******"
    TwitterConsumerSecret = "****** YOUR CONSUMER SECRET KEY ******"
    ```

Known Issues
------------

* Tapping "Sign in with Twitter" and then pressing "cancel" on the authorization screen will cause a crash
* An intermediary error screen is briefly shown between authorization and returning to the app, however does not cause any problems

Credits
-------

![](http://twitter4j.org/en/images/powered-by-badge/powered-by-twitter4j-138x30.png)

This project makes use of the [Twitter4J](http://twitter4j.org/) library (Apache license).

The OAuthActivity class is based off of [Firebase's Login Demo for Android](https://github.com/firebase/firebase-login-demo-android) (MIT license).

License
-------

Licensed under the MIT license. For the full license, please see the `LICENSE` file.