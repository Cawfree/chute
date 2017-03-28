# chute
*Why slow down your UI?* 

Chute is a simple, flexible Android Threading architecture, useful for backgrounding computationally-intensive tasks.

## Background
Chute contains a demonstration of how to decouple processing between the Android's Main (UI) Thread and a Background Thread, using `ThreadHandler`s and `Looper`s. Chute demonstrates a high level interface for defining the separation between computational processes between the background and foreground, as well as managing synchronization.

## Instructions
Download the repository and build the project using `./gradlew build`, and execute the `MainActivity`. Check the Logcat output to see how the application is segmented into different `Threads`.
