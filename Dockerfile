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

#RUN gradle clean fatJar --no-daemon --stacktrace

RUN gradle clean fatJar

FROM openjdk:11.0.11-9-jre-slim

COPY --from=backend-stage /build/app/build/libs/app-all.jar /app/app-all.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/app-all.jar"]
