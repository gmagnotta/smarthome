FROM registry.fedoraproject.org/fedora:32

RUN dnf -y install java-11-openjdk \
    && dnf clean all \
    && rm -rf /var/cache/yum

COPY target /opt/quarkus

WORKDIR /opt/quarkus

ENTRYPOINT ["java",  "-jar", "camel-quarkus-iot-1.0.0-SNAPSHOT-runner.jar"]
