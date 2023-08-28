FROM openjdk:17-alpine

WORKDIR /tmp/petclinic-project
COPY target/*.jar /tmp/petclinic-project/webapp.jar

WORKDIR logs
CMD ["/bin/sh", "-c", "java -jar /tmp/petclinic-project/webapp.jar > web-bootstrap.log"]