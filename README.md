# Demo project

### Steps to reproduce the build:
- Install OpenJDK 11 or AdoptOpenJDK 11
- Install Gradle
- Create backend folder
- Run `gradle init` inside backend folder
```

Welcome to Gradle 7.0.2!

Here are the highlights of this release:
 - File system watching enabled by default
 - Support for running with and building Java 16 projects
 - Native support for Apple Silicon processors
 - Dependency catalog feature preview

For more details see https://docs.gradle.org/7.0.2/release-notes.html

Starting a Gradle Daemon (subsequent builds will be faster)

Select type of project to generate:
  1: basic
  2: application
  3: library
  4: Gradle plugin
Enter selection (default: basic) [1..4] 2

Select implementation language:
  1: C++
  2: Groovy
  3: Java
  4: Kotlin
  5: Scala
  6: Swift
Enter selection (default: Java) [1..6] 4

Split functionality across multiple subprojects?:
  1: no - only one application project
  2: yes - application and library projects
Enter selection (default: no - only one application project) [1..2] 1

Select build script DSL:
  1: Groovy
  2: Kotlin
Enter selection (default: Kotlin) [1..2] 2

Project name (default: backend):
Source package (default: backend):

> Task :init
Get more help with your project: https://docs.gradle.org/7.0.2/samples/sample_building_kotlin_applications.html

BUILD SUCCESSFUL in 1m 20s
2 actionable tasks: 2 executed
```
- Test the project using `./gradlew clean run`
- Add `implementation("io.javalin:javalin:3.13.7")` inside dependencies in `backend/app/build.gradle.kts`
- Add `implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")` inside dependencies in `backend/app/build.gradle.kts`
- Modify `backend/app/src/main/kotlin/backend/App.kt` with sample code, it's important to parse the PORT environment variable for heroku
```
package backend

import io.javalin.Javalin

fun main(args: Array<String>) {
    val app = Javalin.create().start(getHerokuAssignedPort())
    app.get("/") { ctx -> ctx.result("Hello Heroku") }
}

fun getHerokuAssignedPort(): Int {
    val port: String = System.getenv("PORT") ?: "7000"
    return port.toInt()
}
```
- Run `./gradlew clean run` again then test it with `curl http://localhost:7000/`
- Create frontend folder
- Install node.js, install yarn `npm install -g yarn`
- Create new react-typescript app into frontend folder from root project directory with `yarn create @vitejs/app`
- Test it with `cd frontend && yarn && yarn dev` and visit `localhost:3000`
- Test building in production with `yarn build && yarn serve`
- Copy all the content inside dist into `backend/app/src/main/resources/public`
- Modify main function in `backend/app/src/main/kotlin/backend/App.kt`
```
fun main(args: Array<String>) {
    val app = Javalin.create{config ->
        config.addStaticFiles("/public")
    }.start(getHerokuAssignedPort())

    app.get("/api/") { ctx -> ctx.result("Hello Heroku") }
}
```
- Run `./gradlew clean run` and visit localhost:7000, you should see react app, and on `localhost:7000/api/` it should return `Hello...`
- Delete the content of public and keep an empty index.html
- Add fatJar task in build gradle
- Delete default test case
- Create Dockerfile to build frontned, copy content to `backend/app/src/main/resources/public`, then build backend, and run it
- Test docker image
- Push image to heroku
- Test it
- Create GitHub Actions to rebuild and redeploy everytime a new change is pushed to master
- It is recommended to utilize git-flow (main - dev) branch

### Stack:
- Kotlin
- Gradle
- Javalin
- React
- Vite
- TypeScript
- Docker
- Heroku
