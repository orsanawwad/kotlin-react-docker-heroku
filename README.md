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
```
val mainClass = "backend.AppKt"

tasks {
  register("fatJar", Jar::class.java) {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
      attributes("Main-Class" to mainClass)
    }
    from(configurations.runtimeClasspath.get()
        .onEach { println("add from dependencies: ${it.name}") }
        .map { if (it.isDirectory) it else zipTree(it) })
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
    from(sourcesMain.output)
  }
}
```
- Delete default test case so it doesn't error out on undefined `App`
```
package backend

import kotlin.test.Test
import kotlin.test.assertNotNull

class AppTest {
}
```
- Create Dockerfile to build frontned, copy content to `backend/app/src/main/resources/public`, then build backend, and run it (Note that we're using fatJar task here and you might need to do some adjustments)
```

FROM node:14-alpine AS frontend-stage

WORKDIR /app

COPY frontend/. ./

RUN ls

# Alternative to npm ci

RUN rm -rf node_modules && yarn install --frozen-lockfile

RUN yarn build

FROM gradle:7.0.2 as backend-stage

WORKDIR /build

COPY backend/app ./app

COPY backend/settings.gradle.kts ./

COPY --from=frontend-stage /app/dist/. ./app/src/main/resources/public

RUN gradle clean fatJar

FROM openjdk:11.0.11-9-jre-slim

COPY --from=backend-stage /build/app/build/libs/app-all.jar /app/app-all.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/app-all.jar"]
```
- Test docker image
```
docker build -t test .
docker run --rm -p 7000:7000 test
docker rmi test
```
- Create GitHub Actions to build and deploy a new image to heroku on every change to main branch
```
# inside github/workflows/main.yml
name: Deploy

on:
  push:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: akhileshns/heroku-deploy@v3.12.12 # This is the action
        with:
          heroku_api_key: ${{secrets.HEROKU_API_KEY}}
          heroku_app_name: ${{secrets.HEROKU_PROJECT}} 
          heroku_email: ${{secrets.HEROKU_EMAIL}}
          usedocker: true
```
- Create heroku account if you haven't already
- Create new heroku app
- Go to your account settings and get your API
- Go to your repository setting -> secrets -> add each secret (refer to main.yml)
- Push changes, check github actions tab in your repo, then visit APPNAME.heroku.com, you should be able to see your react app, and the api on /api/
- Thats all, this is very basic and might require additional effort and changes to make it work for your project
- Good luck!

### Stack:
- Kotlin
- Gradle
- Javalin
- React
- Vite
- TypeScript
- Docker
- Heroku
