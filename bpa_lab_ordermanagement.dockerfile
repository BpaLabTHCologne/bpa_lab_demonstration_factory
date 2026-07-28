FROM gradle:jdk21-alpine AS build
LABEL authors="uwep"

WORKDIR /workdir/bpa_lab_ordermanagement_process
COPY bpa_lab_ordermanagement_process/build.gradle /workdir/bpa_lab_ordermanagement_process/build.gradle
COPY bpa_lab_ordermanagement_process/settings.gradle /workdir/bpa_lab_ordermanagement_process/settings.gradle
COPY bpa_lab_ordermanagement_process/src /workdir/bpa_lab_ordermanagement_process/src
COPY bpa_lab_process_common/src /workdir/bpa_lab_process_common/src
#COPY bpa_lab_ordermanagement_process/bpmn /workdir/bpa_lab_ordermanagement_process/bpmn
# CMD ["gradle", "clean", "bootRun"]
RUN gradle clean build -x test
#CMD ["java", "-jar", "build/libs/bpa_lab_ordermanagement_process.jar"]
#CMD ["/bin/sh"]
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /workdir/bpa_lab_ordermanagement_process
COPY --from=build /workdir/bpa_lab_ordermanagement_process/build/libs/bpa_lab_ordermanagement_process.jar bpa_lab_ordermanagement_process.jar
COPY bpa_lab_ordermanagement_process/bpmn /workdir/bpa_lab_ordermanagement_process/bpmn
CMD ["java", "-jar", "bpa_lab_ordermanagement_process.jar"]
