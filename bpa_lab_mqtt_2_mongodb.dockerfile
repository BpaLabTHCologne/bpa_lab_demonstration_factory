FROM gradle:jdk25-alpine AS build
LABEL authors="uwep"

WORKDIR /workdir/bpalab_mqtt_2_mongodb
COPY bpalab_mqtt_2_mongodb/build.gradle.kts /workdir/bpalab_mqtt_2_mongodb/build.gradle.kts
COPY bpalab_mqtt_2_mongodb/settings.gradle.kts /workdir/bpalab_mqtt_2_mongodb/settings.gradle.kts
COPY bpalab_mqtt_2_mongodb/src /workdir/bpalab_mqtt_2_mongodb/src
RUN gradle clean build -x test

FROM eclipse-temurin:25-jre-jammy AS runtime
WORKDIR /workdir/bpalab_mqtt_2_mongodb
COPY --from=build /workdir/bpalab_mqtt_2_mongodb/build/libs/bpalab_mqtt_2_mongodb.jar bpalab_mqtt_2_mongodb.jar
CMD ["java", "-jar", "bpalab_mqtt_2_mongodb.jar"]
