FROM gradle:jdk21-alpine AS build
LABEL authors="uwep"

WORKDIR /workdir/bpa_lab_fake_factory
COPY bpa_lab_fake_factory/build.gradle.kts /workdir/bpa_lab_fake_factory/build.gradle.kts
COPY bpa_lab_fake_factory/settings.gradle.kts /workdir/bpa_lab_fake_factory/settings.gradle.kts
COPY bpa_lab_fake_factory/src /workdir/bpa_lab_fake_factory/src
RUN gradle clean build -x test
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /workdir/bpa_lab_fake_factory
COPY bpa_lab_fake_factory/config /workdir/bpa_lab_fake_factory/config
COPY --from=build /workdir/bpa_lab_fake_factory/build/libs/BpaLabFakeFactory.jar BpaLabFakeFactory.jar
CMD ["java", "-jar", "BpaLabFakeFactory.jar"]
