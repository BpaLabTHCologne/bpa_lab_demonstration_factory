FROM gradle:jdk21-alpine AS build
LABEL authors="uwep"

WORKDIR /workdir/bpa_lab_manufacturing_process
COPY bpa_lab_manufacturing_process/build.gradle /workdir/bpa_lab_manufacturing_process/build.gradle
COPY bpa_lab_manufacturing_process/settings.gradle /workdir/bpa_lab_manufacturing_process/settings.gradle
COPY bpa_lab_manufacturing_process/src /workdir/bpa_lab_manufacturing_process/src
RUN gradle clean build -x test
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /workdir/bpa_lab_manufacturing_process
COPY --from=build /workdir/bpa_lab_manufacturing_process/build/libs/bpa_lab_manufacturing_process.jar bpa_lab_manufacturing_process.jar
COPY bpa_lab_manufacturing_process/bpmn /workdir/bpa_lab_manufacturing_process/bpmn
CMD ["java", "-jar", "bpa_lab_manufacturing_process.jar"]
