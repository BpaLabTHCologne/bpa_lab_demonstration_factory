FROM gradle:jdk21-alpine
LABEL authors="uwep"

WORKDIR /workdir/bpa_lab_manufacturing_process
COPY bpa_lab_manufacturing_process/build.gradle /workdir/bpa_lab_manufacturing_process/build.gradle
COPY bpa_lab_manufacturing_process/settings.gradle /workdir/bpa_lab_manufacturing_process/settings.gradle
COPY bpa_lab_manufacturing_process/src /workdir/bpa_lab_manufacturing_process/src
COPY bpa_lab_manufacturing_process/bpmn /workdir/bpa_lab_manufacturing_process/bpmn
CMD ["gradle", "clean", "bootRun"]
