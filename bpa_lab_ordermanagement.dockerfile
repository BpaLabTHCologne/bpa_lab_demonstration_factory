FROM gradle:jdk21-alpine
LABEL authors="uwep"

WORKDIR /workdir/bpa_lab_ordermanagement_process
COPY bpa_lab_ordermanagement_process/build.gradle /workdir/bpa_lab_ordermanagement_process/build.gradle
COPY bpa_lab_ordermanagement_process/settings.gradle /workdir/bpa_lab_ordermanagement_process/settings.gradle
COPY bpa_lab_ordermanagement_process/src /workdir/bpa_lab_ordermanagement_process/src
COPY bpa_lab_process_common/src /workdir/bpa_lab_process_common/src
COPY bpa_lab_ordermanagement_process/bpmn /workdir/bpa_lab_ordermanagement_process/bpmn
CMD ["gradle", "clean", "bootRun"]
