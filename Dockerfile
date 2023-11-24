FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN ./gradlew build -x test

CMD ["./gradlew", "bootRun"]
